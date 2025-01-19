package me.aloic.lazybot.discord.util;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Optional;

public class OptionMappingTool
{
    public static String getOptionOrException(OptionMapping option)
    {
       return Optional.ofNullable(option).orElseThrow(() -> new RuntimeException("必须参数为被正确传递")).getAsString();
    }
    public static String getOptionOrException(OptionMapping option, String message)
    {
        return Optional.ofNullable(option).orElseThrow(() -> new RuntimeException(message)).getAsString();
    }

    public static String getOptionOrDefault(OptionMapping option,@Nonnull String defaultValue)
    {
        try {
           return option.getAsString();
        } catch (Exception e) {
          return defaultValue;
        }
    }
    public static  <T>T getOptionOrDefault(OptionMapping option,@Nonnull T defaultValue)
    {
        try {
            return (T) option.getAsString();

        } catch (Exception e) {
            return defaultValue;
        }
    }
    public static Integer getOptionOrDefault(OptionMapping option,@Nonnull Integer defaultValue)
    {
        try {
            return option.getAsInt();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
