package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotNotFoundException;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatisticsPO;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatsSnapshotTarget;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatsWatchPO;
import me.aloic.lazybot.osu.dao.mapper.PlayerStatisticsMapper;
import me.aloic.lazybot.osu.dao.mapper.PlayerStatsWatchMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.SupportedSubServer;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.osu.service.PlayerStatisticsService;
import me.aloic.lazybot.util.DataExtractor;
import me.aloic.lazybot.osu.utils.PlayerStatsRequestLimiter;
import me.aloic.lazybot.osu.utils.PlayerStatsTableContext;
import me.aloic.lazybot.osu.utils.PlayerStatsTableManager;
import me.aloic.lazybot.osu.utils.PlayerStatsWatchManager;
import me.aloic.lazybot.util.TransformerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class PlayerStatisticsServiceImpl implements PlayerStatisticsService
{
    @Value("${lazybot.player-stats.threads:8}")
    private int snapshotThreads;
    @Value("${lazybot.player-stats.requests-per-minute:400}")
    private int requestsPerMinute;

    @Resource
    private PlayerStatsWatchMapper watchMapper;
    @Resource
    private PlayerStatisticsMapper playerStatisticsMapper;
    @Resource
    private PlayerStatsTableManager tableManager;
    @Resource
    private PlayerStatsWatchManager watchManager;
    @Resource
    private DataExtractor dataExtractor;
    @Resource
    private TokenMonitor tokenMonitor;


    private static final int WRITE_BATCH_SIZE = 100;
    private final List<PlayerStatisticsPO> stats = new ArrayList<>();
    private final List<PlayerStatsWatchPO> watches = new ArrayList<>();
    private final Object writeLock = new Object();

    @Override
    public synchronized void runDailySnapshot()
    {
        LocalDate snapshotDate = LocalDate.now(PlayerStatsTableManager.ZONE);
        LocalDateTime recordDateTime = snapshotDate.atStartOfDay();
        int year = snapshotDate.getYear();
        tableManager.ensureYear(year);
        tokenMonitor.refreshClientToken();

        int subserver = SupportedSubServer.STABLE.getValue();
        List<PlayerStatsSnapshotTarget> targets = watchMapper.selectActiveSnapshotTargets(subserver);
        if (CollectionUtils.isEmpty(targets)) {
            log.info("Player stats snapshot skipped: no active user/mode targets");
            return;
        }

        int threads = Math.max(1, snapshotThreads);
        int rpm = Math.max(1, requestsPerMinute);
        int total = targets.size();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        PlayerStatsRequestLimiter limiter = new PlayerStatsRequestLimiter(rpm);

        log.info("Player stats snapshot started: date={}, requests={}, threads={}, rpm={}", snapshotDate, total, threads, rpm);

        synchronized (writeLock) {
            stats.clear();
            watches.clear();
        }
        try (ExecutorService pool = Executors.newFixedThreadPool(threads, Thread.ofVirtual().name("player-stats-", 0).factory())) {
            List<CompletableFuture<Void>> futures = new ArrayList<>(total);
            for (PlayerStatsSnapshotTarget target : targets) {
                futures.add(CompletableFuture.runAsync(
                        () -> snapshotOne(target, recordDateTime, year, subserver, limiter, success, failed, total),
                        pool)
                );
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
        synchronized (writeLock) {
            flushWrites(year);
        }

        log.info("Player stats snapshot finished: date={}, success={}, failed={}",
                snapshotDate, success.get(), failed.get());
    }

    private void snapshotOne(PlayerStatsSnapshotTarget target,
                             LocalDateTime recordDateTime,
                             int year,
                             int subserver,
                             PlayerStatsRequestLimiter limiter,
                             AtomicInteger success,
                             AtomicInteger failed,
                             int total)
    {
        Integer userId = target.getUserId();
        OsuMode mode = OsuMode.getMode(target.getMode());
        try {
            limiter.acquire();
            PlayerInfoDTO player = dataExtractor.extractPlayerInfoRaw(userId, mode.getDescribe());
            PlayerStatisticsPO row = TransformerUtil.transformToPlayerStatisticsPO(player, mode.getValue(), subserver, recordDateTime);
            bufferAndFlush(year, row, PlayerStatsWatchManager.buildRecord(
                    userId, mode.getValue(), subserver, PlayerStatsWatchManager.isActiveUser(row)));
            int doneCount = success.incrementAndGet() + failed.get();
            if (doneCount % 200 == 0 || doneCount == total) {
                log.info("Player stats snapshot progress: {}/{}", doneCount, total);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failed.incrementAndGet();
            log.warn("Player stats snapshot interrupted: userId={}, mode={}", userId, mode.getDescribe());
        }
        catch (LazybotNotFoundException e) {
            failed.incrementAndGet();
            bufferAndFlush(year, null, PlayerStatsWatchManager.buildRecord(userId, mode.getValue(), subserver, false));
            log.warn("Player stats snapshot user not found, probably banned?: userId={}, mode={}", userId, mode.getDescribe());
        }
        catch (Exception e) {
            failed.incrementAndGet();
            log.error("Player stats snapshot failed: userId={}, mode={}, {}", userId, mode.getDescribe(), e.getMessage(), e);
        }
    }

    private void bufferAndFlush(int year, PlayerStatisticsPO row, PlayerStatsWatchPO watch)
    {
        synchronized (writeLock) {
            if (row != null) {
                stats.add(row);
            }
            if (watch != null) {
                watches.add(watch);
            }
            if (stats.size() >= WRITE_BATCH_SIZE || watches.size() >= WRITE_BATCH_SIZE) {
                flushWrites(year);
            }
        }
    }

    private void flushWrites(int year)
    {
        try {
            if (!stats.isEmpty()) {
                PlayerStatsTableContext.run(year, () -> playerStatisticsMapper.upsertBatch(stats));
                stats.clear();
            }
            if (!watches.isEmpty()) {
                watchManager.recordPlayerActivityBatch(watches);
                watches.clear();
            }
        }
        catch (Exception e) {
            stats.clear();
            watches.clear();
            log.error("Player stats snapshot batch write failed: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<PlayerStatisticsPO> findRange(Integer osuUserId,
                                              Integer mode,
                                              Integer subserver,
                                              LocalDate from,
                                              LocalDate toInclusive)
    {
        if (osuUserId == null || mode == null || subserver == null || from == null || toInclusive == null) {
            return List.of();
        }
        if (toInclusive.isBefore(from)) {
            return List.of();
        }
        List<PlayerStatisticsPO> result = new ArrayList<>();
        for (int year = from.getYear(); year <= toInclusive.getYear(); year++) {
            if (!tableManager.existsYear(year)) {
                continue;
            }
            LocalDate yearStart = LocalDate.of(year, 1, 1);
            LocalDate yearEnd = LocalDate.of(year, 12, 31);
            LocalDate queryFrom = from.isAfter(yearStart) ? from : yearStart;
            LocalDate queryTo = toInclusive.isBefore(yearEnd) ? toInclusive : yearEnd;
            List<PlayerStatisticsPO> yearRows = PlayerStatsTableContext.call(year, () ->
                    playerStatisticsMapper.selectRange(
                            osuUserId,
                            mode,
                            subserver,
                            queryFrom.atStartOfDay(),
                            queryTo.plusDays(1).atStartOfDay()));
            result.addAll(yearRows);
        }
        return result;
    }
}
