package me.aloic.lazybot.command;

import com.mikuac.shiro.core.Bot;
import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.HelpFormatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public interface LazybotSlashCommand
{
    static final Object NO_SUMMARY = new Object();
    static final ClassValue<Object> SUMMARY_FIELDS = new ClassValue<>()
    {
        @Override
        protected Object computeValue(Class<?> type)
        {
            try {
                Field field = type.getDeclaredField("SUMMARY");
                if (!Modifier.isStatic(field.getModifiers())
                        || !CommandSummary.class.isAssignableFrom(field.getType())) {
                    return NO_SUMMARY;
                }
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException exception) {
                return NO_SUMMARY;
            }
        }
    };

    void execute(SlashCommandInteractionEvent event) throws Exception;
    void execute(Bot bot, LazybotSlashCommandEvent event) throws Exception;
    void execute(LazybotSlashCommandEvent event) throws Exception;

    default CommandHelp commandHelp() {
        return null;
    }

    default CommandSummary getCommandSummary() {
        Object cached = SUMMARY_FIELDS.get(AopProxyUtils.ultimateTargetClass(this));
        if (!(cached instanceof Field field)) {
            return null;
        }
        try {
            return (CommandSummary) field.get(null);
        }
        catch (IllegalAccessException exception) {
            return null;
        }
    }

    default List<CommandSummary> getCommandSummaries() {
        CommandSummary summary = getCommandSummary();
        return summary == null ? List.of() : List.of(summary);
    }

    default String getHelp() {
        CommandHelp help = commandHelp();
        if (help == null) {
            return "[Lazybot] 暂无帮助文档";
        }
        return HelpFormatter.format(help);
    }
}
