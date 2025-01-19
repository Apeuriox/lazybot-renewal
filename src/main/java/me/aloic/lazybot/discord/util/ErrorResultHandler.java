package me.aloic.lazybot.discord.util;

import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ErrorResultHandler
{
    public static void createBindError(SlashCommandInteractionEvent event, UserTokenPO user) {
        event.getHook().sendMessage("您已绑定用户: " + user.getPlayer_name() ).queue();
    }
    public static void createNotBindOsuError(SlashCommandInteractionEvent event) {
        event.getHook().sendMessage("请先使用/link绑定osu账号").queue();
    }
    public static void createNoSuchUserError(SlashCommandInteractionEvent event,String username) {
        event.getHook().sendMessage("此用户不存在: " + username).queue();
    }
    public static void createParameterError(SlashCommandInteractionEvent event) {
        event.getHook().sendMessage("请输入必须参数").queue();
    }
    public static void createExceptionMessage(SlashCommandInteractionEvent event, Exception e) {
        event.getHook().sendMessage(e.getMessage()).queue();
    }
}
