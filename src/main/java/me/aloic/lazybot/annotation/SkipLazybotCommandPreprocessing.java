package me.aloic.lazybot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a command whose arguments must remain untouched by the common OneBot
 * mode, panel-version, punctuation, whitespace, and lowercase preprocessing.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipLazybotCommandPreprocessing
{
}
