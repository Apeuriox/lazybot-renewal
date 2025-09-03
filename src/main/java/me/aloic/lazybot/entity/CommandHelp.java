package me.aloic.lazybot.entity;
import lombok.Data;

import java.util.*;

@Data
public class CommandHelp
{
    private final String command;
    private final String alias;
    private final String description;
    private final List<String> usageExamples;
    private final  List<CommandParameter> options;
    private final String creator;
    private final String designer;
    private final String initialReleaseDate;

    public CommandHelp(String command,String alias, String description, String creator, String designer, String initialReleaseDate) {
        this.command = command;
        this.alias = alias;
        this.description = description;
        this.creator = creator;
        this.designer = designer;
        this.initialReleaseDate = initialReleaseDate;
        this.usageExamples = new ArrayList<>();
        this.options = new ArrayList<>();
    }

    public CommandHelp addExample(String example) {
        usageExamples.add(example);
        return this;
    }

    public CommandHelp addOption(CommandParameter parameter) {
        options.add(parameter);
        return this;
    }

}
