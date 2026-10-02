package me.aloic.lazybot.monitor;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.CommandCallRecord;
import me.aloic.lazybot.entity.CommandStat;
import me.aloic.lazybot.osu.dao.entity.po.CommandUsage;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageCommand;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageSource;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUsageTimeDistribution;
import me.aloic.lazybot.osu.dao.mapper.UsageMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Component
public class CommandMonitor {

    @Resource
    private UsageMapper usageMapper;
    private final ConcurrentMap<String, CommandStat> commandStats = new ConcurrentHashMap<>();
    private LocalDateTime startTime;

    public CommandMonitor() {
        this.startTime = LocalDateTime .now();
    }

    public void record(String commandName, String userId, String channelId) {
        commandStats
            .computeIfAbsent(commandName, CommandStat::new)
            .recordCall(userId, channelId);
    }

    public Map<String, CommandStat> getAllStats() {
        return new HashMap<>(commandStats);
    }

    public void printStatsTest() {
        //only fot test use
        commandStats.forEach((cmd, stat) -> {
            System.out.println("Command: " + cmd + ", Count: " + stat.getCallCount());
        });
    }

    public void clear() {
        commandStats.clear();
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
    public void clearOldStats() {
        log.info("正在保存旧数据...");
        usageMapper.insert(setupCommandUsage(commandStats, startTime));
        clear();
        this.startTime = LocalDateTime.now();
    }

    public static CommandUsage setupCommandUsage(Map<String, CommandStat> commandStatMap, LocalDateTime startTime)
    {
        if (commandStatMap.isEmpty()){
            return new CommandUsage(0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0, startTime);
        }
        AtomicInteger indexSource = new AtomicInteger(1);
        List<LazybotUsageSource> sourceList = commandStatMap.values().stream()
                .flatMap(stat -> stat.getCallRecords().stream())
                .collect(Collectors.groupingBy(CommandCallRecord::channelId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new LazybotUsageSource(indexSource.getAndIncrement(), entry.getKey(), entry.getValue().intValue()))
                .toList();


        List<LazybotUsageTimeDistribution> timeDistList = setupTimeDistribution(commandStatMap);

        List<LazybotUsageCommand> commandList = commandStatMap.entrySet().stream()
                .map(entry -> new LazybotUsageCommand(entry.getValue().getCallRecords().size(), entry.getKey()))
                .sorted(Comparator.comparingInt(LazybotUsageCommand::getCount).reversed())
                .toList();

        int totalCount = commandStatMap.values().stream()
                .mapToInt(stat -> stat.getCallRecords().size())
                .sum();

        int isComplete = startTime.getHour() == 0 ? 1 : 0;

        return new CommandUsage(totalCount, timeDistList, sourceList, commandList, isComplete, startTime);
    }

    //fill empty data with 0
    private static List<LazybotUsageTimeDistribution> setupTimeDistribution(Map<String, CommandStat> commandStatMap)
    {
        int[] hourCount = new int[24];
        commandStatMap.values().forEach(stat ->
                stat.getCallRecords().forEach(record -> {
                    int hour = (record.timestamp().getHour() - 1 + 24 ) % 24;
                    hourCount[hour]++;
                })
        );
        return IntStream.range(0, 24)
                .mapToObj(hour -> new LazybotUsageTimeDistribution(hour, hourCount[hour]))
                .toList();
    }

}
