package me.aloic.lazybot.discord.config;

import jakarta.annotation.Resource;
import me.aloic.lazybot.command.core.CommandDefinition;
import me.aloic.lazybot.command.core.CommandOptionDefinition;
import me.aloic.lazybot.command.registry.PlatformIndependentCommandRegistry;
import me.aloic.lazybot.discord.DiscordBotFactory;
import me.aloic.lazybot.discord.entity.CommandOption;
import me.aloic.lazybot.discord.enums.CommandEnum;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DiscordBotRunner implements ApplicationRunner
{
    @Resource
    private DiscordBotFactory discordBotFactory;
    @Resource
    private PlatformIndependentCommandRegistry independentCommandRegistry;
    @Value("${discord.bot.id}")
    private String botId;

    private JDA botInstance;

    @Value("${lazybot.global.discord.enabled}")
    private Boolean enabled;

    private static final Logger logger = LoggerFactory.getLogger(DiscordBotRunner.class);

    @Override
    public void run(ApplicationArguments args)
    {
        logger.info("Discord端启用: {}",enabled);
        if(enabled)
        {
            logger.info("正在初始化Discord服务");
            botInstance = discordBotFactory.createBotInstance();
            initCommands();
        }
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
        Map<String, CommandDefinition> independentDefinitions = new LinkedHashMap<>();
        independentCommandRegistry.commands().stream()
                .map(command -> command.definition())
                .filter(CommandDefinition::discordEnabled)
                .forEach(definition -> independentDefinitions.put(definition.name().toLowerCase(), definition));

        for(CommandEnum commandEnum : CommandEnum.values()){
            if(commandEnum.getValid() && !independentDefinitions.containsKey(commandEnum.getCommandName().toLowerCase())){
              commandList.add(setupSlashCommand(commandEnum));
            }
        }
        independentDefinitions.values().stream()
                .map(this::setupSlashCommand)
                .forEach(commandList::add);
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

    private SlashCommandData setupSlashCommand(CommandDefinition definition) {
        SlashCommandData command = Commands.slash(definition.name(), definition.description());
        for (CommandOptionDefinition option : definition.options()) {
            command = command.addOption(
                    toDiscordOptionType(option),
                    option.name(),
                    option.description(),
                    option.required(),
                    option.autoComplete()
            );
        }
        return command;
    }

    private OptionType toDiscordOptionType(CommandOptionDefinition option) {
        return switch (option.type()) {
            case STRING -> OptionType.STRING;
            case INTEGER -> OptionType.INTEGER;
            case BOOLEAN -> OptionType.BOOLEAN;
            case NUMBER -> OptionType.NUMBER;
        };
    }
}
