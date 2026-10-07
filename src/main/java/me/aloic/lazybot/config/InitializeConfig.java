package me.aloic.lazybot.config;

import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.osu.utils.PlayerStatsTableManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitializeConfig  implements ApplicationRunner
{

    private final TokenMonitor tokenMonitor;
    private final PlayerStatsTableManager playerStatsTableManager;

    public InitializeConfig(TokenMonitor tokenMonitor,
                            PlayerStatsTableManager playerStatsTableManager)
    {
        this.tokenMonitor = tokenMonitor;
        this.playerStatsTableManager = playerStatsTableManager;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        ResourceMonitor.initResources();
        playerStatsTableManager.ensureCurrentAndNextYearsTable();
        tokenMonitor.refreshClientToken();
        tokenMonitor.refreshPPPlusClientToken();
    }
}
