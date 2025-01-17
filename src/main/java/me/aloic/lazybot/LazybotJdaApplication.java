package me.aloic.lazybot;

import com.mikuac.shiro.annotation.common.Shiro;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class LazybotJdaApplication
{

    public static void main(String[] args)
    {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("com.mikuac.shiro").setLevel(Level.DEBUG);
        SpringApplication.run(LazybotJdaApplication.class, args);
    }

}
