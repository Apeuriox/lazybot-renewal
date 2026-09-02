package me.aloic.lazybot.osu.utils;

import java.util.function.Supplier;

public final class PlayerStatsTableContext
{
    private static final ThreadLocal<Integer> YEAR = new ThreadLocal<>();

    private PlayerStatsTableContext() {}

    public static void setYear(int year)
    {
        YEAR.set(year);
    }

    public static Integer getYear()
    {
        return YEAR.get();
    }

    public static void clear()
    {
        YEAR.remove();
    }

    public static <T> T call(int year, Supplier<T> action)
    {
        setYear(year);
        try {
            return action.get();
        }
        finally {
            clear();
        }
    }

    public static void run(int year, Runnable action)
    {
        setYear(year);
        try {
            action.run();
        }
        finally {
            clear();
        }
    }
}
