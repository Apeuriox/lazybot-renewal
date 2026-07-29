package me.aloic.lazybot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig implements AsyncConfigurer {
    private static final Logger log = LoggerFactory.getLogger(ExecutorConfig.class);


    @Bean(name = "virtualThreadExecutor", destroyMethod = "close")
    public ExecutorService virtualThreadExecutor() {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                        .name("lazybot-command-", 0)
                        .factory());
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, obj) -> {
            log.error("Async method {} threw an exception", method.getName(), throwable);
            for (Object param : obj) {
                log.info("Parameter value - {}", param);
            }
        };
    }
}
