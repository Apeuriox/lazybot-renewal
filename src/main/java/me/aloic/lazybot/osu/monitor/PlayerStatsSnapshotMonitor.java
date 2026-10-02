package me.aloic.lazybot.osu.monitor;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.osu.service.PlayerStatisticsService;
import me.aloic.lazybot.osu.utils.PlayerStatsTableManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Year;

@Slf4j
@Component
@ConditionalOnProperty(name = "lazybot.player-stats.snapshot-enabled", matchIfMissing = true)
public class PlayerStatsSnapshotMonitor
{
    @Resource
    private PlayerStatisticsService playerStatisticsService;
    @Resource
    private PlayerStatsTableManager tableManager;

    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Shanghai")
    public void snapshotDaily()
    {
        log.info("Starting daily player stats snapshot");
        playerStatisticsService.runDailySnapshot();
    }

    @Scheduled(cron = "0 0 0 1 12 ?", zone = "Asia/Shanghai")
    public void prepareNextYearTable()
    {
        int nextYear = Year.now(PlayerStatsTableManager.ZONE).getValue() + 1;
        log.info("Preparing player stats table for {}", nextYear);
        tableManager.ensureYear(nextYear);
    }
}
