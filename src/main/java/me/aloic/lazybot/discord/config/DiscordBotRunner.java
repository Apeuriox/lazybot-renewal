package me.aloic.lazybot.discord.config;

import jakarta.annotation.Resource;
import me.aloic.lazybot.discord.DiscordBotFactory;
import me.aloic.lazybot.discord.entity.CommandOption;
import me.aloic.lazybot.discord.enums.CommandEnum;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DiscordBotRunner implements ApplicationRunner
{
    @Resource
    private DiscordBotFactory botFactory;
    @Value("${discord.bot.id}")
    private String botId;

    private JDA botInstance;

    private static final Logger logger = LoggerFactory.getLogger(DiscordBotRunner.class);
    @Resource
    private TokenMonitor tokenMonitor;

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        botInstance = botFactory.createBotInstance();
        initCommands();
        ResourceMonitor.initResources();
        tokenMonitor.refreshClientToken();
    }
    private void initCommands(){
        Optional.ofNullable(botInstance)
                .ifPresentOrElse(
                        this::setupSlashCommand,
                        () ->  logger.info("Bot实例为空")
                );
    }
    private void setupSlashCommand(@NotNull JDA instance)
    {
        List<SlashCommandData> commandList = new ArrayList<>();
        for(CommandEnum commandEnum : CommandEnum.values()){
            if(commandEnum.getValid()){
              commandList.add(setupSlashCommand(commandEnum));
            }
        }
        instance.updateCommands().addCommands(commandList).queue();
        logger.info("命令更新完成");
    }

    private SlashCommandData setupSlashCommand(CommandEnum commandEnum)
    {
        return setupSlashCommand(Commands.slash(commandEnum.getCommandName(), commandEnum.getDescription()),commandEnum);
    }
    private SlashCommandData setupSlashCommand(SlashCommandData options, CommandEnum commandEnum ){
        if (commandEnum.getOptions() == null) return options;
        for(CommandOption option : commandEnum.getOptions()){
            options = options.addOption(option.getType(), option.getName(), option.getDescription(), option.getRequired(), option.getAutoComplete());
        }
        return options;
    }
}
