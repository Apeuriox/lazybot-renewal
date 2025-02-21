package me.aloic.lazybot.component;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import me.aloic.lazybot.command.LazybotSlashCommand;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SlashCommandProcessor
{
    @Resource
    private LazybotSlashCommandRegistry registry;

    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessor.class);

    @Async("asyncServiceExecutor")
    public void processDiscord(SlashCommandInteractionEvent event)
    {
        try {
            LazybotSlashCommand command = registry.getCommand(event.getName());
            if (command != null) {
                logger.info("正在处理 {} 命令",event.getName());
                command.execute(event);
            } else {
                event.reply("找不到对应指令").setEphemeral(true).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResultHandler.createExceptionMessage(event, e);
        }
    }
    @Async("asyncServiceExecutor")
    public void processQQ(Bot bot, LazybotSlashCommandEvent event)
    {
            try {
                LazybotSlashCommand command = registry.getCommand(event.getCommandType());
                if (command != null) {
                    logger.info("正在处理 {} 命令(onebot)", event.getCommandType());
                    command.execute(bot,event);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //todo: 处理异常
                bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text(e.getMessage()).build(),false);
            }
    }
}
