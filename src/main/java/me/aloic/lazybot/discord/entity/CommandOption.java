package me.aloic.lazybot.discord.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Data
@AllArgsConstructor
public class CommandOption
{
    private OptionType type;
    private String name;
    private String description;
    private Boolean required;
    private Boolean autoComplete;
}
