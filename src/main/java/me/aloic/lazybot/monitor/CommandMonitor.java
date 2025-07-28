package me.aloic.lazybot.monitor;

import me.aloic.lazybot.entity.CommandStat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class CommandMonitor {

    private final ConcurrentMap<String, CommandStat> commandStats = new ConcurrentHashMap<>();

    public void record(String commandName, String userId, String channelId) {
        commandStats
            .computeIfAbsent(commandName, CommandStat::new)
            .recordCall(userId, channelId);
    }

    public Map<String, CommandStat> getAllStats() {
        return new HashMap<>(commandStats);
    }

    public void printStatsTest() {
        commandStats.forEach((cmd, stat) -> {
            System.out.println("Command: " + cmd + ", Count: " + stat.getCallCount());
        });
    }

    public void clear() {
        commandStats.clear();
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void clearOldStats() {
        clear();
    }
}
