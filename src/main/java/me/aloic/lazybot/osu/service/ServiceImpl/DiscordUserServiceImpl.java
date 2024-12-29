package me.aloic.lazybot.osu.service.ServiceImpl;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.service.DiscordUserService;
import me.aloic.lazybot.util.DataObjectExtractor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.BiConsumer;

//im too lazy to refactor this
@Service
public class DiscordUserServiceImpl implements DiscordUserService
{
    @Resource
    private TokenMapper tokenMapper;

    @Override
    public void linkUser(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        BiConsumer<SlashCommandInteractionEvent, UserTokenPO> createBindError =  ErrorResultHandler::createBindError;
        if(event.getOption("username")==null)
            ErrorResultHandler.createParameterError(event);
        Optional.ofNullable(tokenMapper.selectByDiscord(event.getUser().getIdLong()))
                .ifPresentOrElse(
                        token -> createBindError.accept(event, token),
                        () -> insertUserToTable(event, event.getOption("username").getAsString()));
    }
    private void insertUserToTable(SlashCommandInteractionEvent event, @Nonnull String username){
        UserTokenPO client = tokenMapper.selectByDiscord(0L);
        PlayerInfoDTO player = DataObjectExtractor.extractPlayerInfo(client.getAccess_token(),username, "osu");
        UserTokenPO user = new UserTokenPO(event.getUser().getIdLong(), player.getId(), player.getUsername());
        Optional.ofNullable(tokenMapper.selectByPlayername(player.getUsername()))
                .ifPresentOrElse(
                        userToken -> tokenMapper.updateByToken(user),
                        () -> tokenMapper.insert(user)
                );
        event.getHook().sendMessage("成功绑定用户: " +username).queue();
    }
}
