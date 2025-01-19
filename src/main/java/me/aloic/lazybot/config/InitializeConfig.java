package me.aloic.lazybot.config;

import jakarta.annotation.Resource;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitializeConfig  implements ApplicationRunner
{

    @Resource
    private TokenMonitor tokenMonitor;

    @Override
    public void run(ApplicationArguments args)
    {
        ResourceMonitor.initResources();
        tokenMonitor.refreshClientToken();
    }
}
