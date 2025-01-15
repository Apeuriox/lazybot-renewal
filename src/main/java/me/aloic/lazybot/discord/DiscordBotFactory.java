package me.aloic.lazybot.discord;
import jakarta.annotation.Resource;
import me.aloic.lazybot.component.EventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

@Component
public class DiscordBotFactory
{
    @Value("${discord.bot.token}")
    private String BOT_TOKEN;

    private JDA instance;
    @Resource
    private EventListener discordEventListener;


    public JDA createBotInstance() {
        Supplier<JDA> instanceSupplier = () -> {
            try {
                return instance = JDABuilder.createDefault(BOT_TOKEN)
                        .setActivity(Activity.playing("Springboot"))
                        .addEventListeners(discordEventListener)
                        .build().awaitReady();
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        };
        return Optional.ofNullable(instance)
                .orElseGet(() -> {
                    if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
                        throw new RuntimeException("BOT_TOKEN is null or empty");
                    }
                    return instanceSupplier.get();
                });
    }

}
