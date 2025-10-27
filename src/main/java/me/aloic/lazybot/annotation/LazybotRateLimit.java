package me.aloic.lazybot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LazybotRateLimit
{
    long capacity();        // 桶容量（允许的突发数量）
    long refillTokens();    // 每次补充的令牌数量
    long refillPeriod();    // 补充周期
    TimeUnit unit() default TimeUnit.SECONDS;
    Scope scope() default Scope.GLOBAL;

    enum Scope { USER, CHANNEL, GLOBAL }
}
