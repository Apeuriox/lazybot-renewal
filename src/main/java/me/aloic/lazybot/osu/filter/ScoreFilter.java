package me.aloic.lazybot.osu.filter;

import org.spring.osu.model.Score;

@FunctionalInterface
public interface ScoreFilter {
    boolean test(Score score);
}
