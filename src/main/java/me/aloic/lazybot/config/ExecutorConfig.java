package me.aloic.lazybot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@EnableAsync
@Configuration
public class ExecutorConfig implements AsyncConfigurer {
    private static final Logger log = LoggerFactory.getLogger(ExecutorConfig.class);


    @Bean(name = "virtualThreadExecutor")
    public Executor asyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
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
