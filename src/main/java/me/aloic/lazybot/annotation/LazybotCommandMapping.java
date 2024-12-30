package me.aloic.lazybot.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LazybotCommandMapping {
    String[] value();
}
