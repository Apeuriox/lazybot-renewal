package me.aloic.lazybot.osu.service;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.command.CommandReply;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.parameter.UpdatePanelVersionParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public interface UserService
{
    void updateDefaultSubset(SlashCommandInteractionEvent event);

    void updateDefaultSubset(Bot bot, LazybotSlashCommandEvent event);

    String updateDefaultSubset(OsuSubruleset ruleset, Long qqCode);

    void linkUser(SlashCommandInteractionEvent event);

    void linkUser(Bot bot, LazybotSlashCommandEvent event);

    void linkUser(CommandReply reply, LazybotSlashCommandEvent event);

    void linkStarMoon(Bot bot, LazybotSlashCommandEvent event);

    void unlinkUser(SlashCommandInteractionEvent event);

    void unlinkUser(Bot bot, LazybotSlashCommandEvent event);

    void unlinkUser(CommandReply reply, LazybotSlashCommandEvent event);

    void updateDefaultSubset(CommandReply reply, LazybotSlashCommandEvent event);

    String updatedUserPreferredPanelVersion(UpdatePanelVersionParameter params);
}
