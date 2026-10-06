package me.aloic.lazybot.entity;

import java.util.List;

public record CommandSummary(
        Category category,
        String command,
        List<String> alias,
        String parameter,
        String example,
        String description,
        String caution,
        String subcommand)
{
    public enum Category
    {
        OSU, CUSTOMIZE, PREFERENCE, FUN, OTHERS
    }

    public CommandSummary(Category category, String command, List<String> alias,
                          String parameter, String example, String description, String caution)
    {
        this(category, command, alias, parameter, example, description, caution, null);
    }

    public CommandSummary
    {
        alias = alias == null ? List.of() : List.copyOf(alias);
    }
}
