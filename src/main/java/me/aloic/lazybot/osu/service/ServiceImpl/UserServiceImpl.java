package me.aloic.lazybot.osu.service.ServiceImpl;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.DataObjectExtractor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.BiConsumer;

//im too lazy to refactor this
@Service
public class UserServiceImpl implements UserService
{
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;



    @Override
    public void updateDefaultMode(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        if(event.getOption("mode")==null) throw new RuntimeException("请输入模式");
        OsuMode mode = OsuMode.getMode(event.getOption("mode").getAsString());
        if (mode == OsuMode.Default) throw new RuntimeException("未知的模式: " + event.getOption("mode").getAsString());
        BiConsumer<SlashCommandInteractionEvent, UserTokenPO> createBindError =  ErrorResultHandler::createBindError;
        if(event.getOption("username")==null)
            ErrorResultHandler.createParameterError(event);
        Optional.ofNullable(discordTokenMapper.selectByDiscord(event.getUser().getIdLong()))
                .ifPresentOrElse(
                        token -> createBindError.accept(event, token),
                        () -> discordTokenMapper.updateDefaultMode(mode.getDescribe().toLowerCase(), event.getUser().getIdLong()));
        event.getHook().sendMessage("已成功更改模式为: " +mode.getDescribe()).queue();
    }
    @Override
    public void updateDefaultMode(Bot bot, LazybotSlashCommandEvent event)
    {
        if (event.getCommandParameters()==null || event.getCommandParameters().isEmpty()) throw new RuntimeException("请输入模式");
        OsuMode mode = OsuMode.getMode(event.getCommandParameters().getFirst());
        if (mode == OsuMode.Default) throw new RuntimeException("未知的模式: " + event.getCommandParameters().getFirst());
        Optional.ofNullable(tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId()))
                .ifPresentOrElse(
                        this::createBindError,
                        () -> tokenMapper.updateDefaultMode(mode.getDescribe().toLowerCase(), event.getMessageEvent().getSender().getUserId()));
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("已成功更改模式为: " +mode.getDescribe()).build(),false);
    }


    @Override
    public void linkUser(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        BiConsumer<SlashCommandInteractionEvent, UserTokenPO> createBindError =  ErrorResultHandler::createBindError;
        if(event.getOption("username")==null)
            ErrorResultHandler.createParameterError(event);
        Optional.ofNullable(discordTokenMapper.selectByDiscord(event.getUser().getIdLong()))
                .ifPresentOrElse(
                        token -> createBindError.accept(event, token),
                        () -> insertUserToTable(event, event.getOption("username").getAsString()));
    }

    @Override
    public void linkUser(Bot bot, LazybotSlashCommandEvent event)
    {
        String username = String.join(" ", event.getCommandParameters());
        Optional.ofNullable(tokenMapper.selectByPlayername(username)).ifPresent(this::createAlreadyBindError);
        Optional.ofNullable(tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId()))
                .ifPresentOrElse(
                        this::createBindError,
                        () -> insertUserToTable(event, username,bot));
    }

    @Override
    public void unlinkUser(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        if(event.getOption("username")==null)
            ErrorResultHandler.createParameterError(event);
        Optional.ofNullable(discordTokenMapper.selectByDiscord(event.getUser().getIdLong()))
                .ifPresentOrElse(token -> discordTokenMapper.deleteByDiscord(event.getUser().getIdLong()),
                        this::createNotBindError);
        event.getHook().sendMessage("已解除绑定: " +event.getOption("username").getAsString()).queue();
    }
    @Override
    public void unlinkUser(Bot bot, LazybotSlashCommandEvent event)
    {
        Optional.ofNullable(tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId()))
                .ifPresentOrElse(
                        token -> tokenMapper.deleteByQQ(event.getMessageEvent().getSender().getUserId()),
                        this::createNotBindError);
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("成功解除绑定").build(),false);
    }
    private void insertUserToTable(SlashCommandInteractionEvent event, @Nonnull String username){
        UserTokenPO client = discordTokenMapper.selectByDiscord(0L);
        PlayerInfoDTO player = DataObjectExtractor.extractPlayerInfo(client.getAccess_token(),username, "osu");
        UserTokenPO user = new UserTokenPO(event.getUser().getIdLong(), player.getId(), player.getUsername());
        Optional.ofNullable(discordTokenMapper.selectByPlayername(player.getUsername()))
                .ifPresentOrElse(
                        userToken -> discordTokenMapper.updateByToken(user),
                        () -> discordTokenMapper.insert(user)
                );
        event.getHook().sendMessage("成功绑定用户: " +username).queue();
    }
    private void insertUserToTable(LazybotSlashCommandEvent event, @Nonnull String username,Bot bot){
        AccessTokenPO client = tokenMapper.selectByQq_code(0L);
        PlayerInfoDTO player = DataObjectExtractor.extractPlayerInfo(client.getAccess_token(),username, "osu");
        AccessTokenPO user = new AccessTokenPO();
        user.setPlayer_id(player.getId());
        user.setPlayer_name(player.getUsername());
        user.setDefault_mode("osu");
        user.setQq_code(event.getMessageEvent().getSender().getUserId());
        user.setValid(1);
        Optional.ofNullable(tokenMapper.selectByPlayername(player.getUsername()))
                .ifPresentOrElse(
                        userToken -> tokenMapper.updateByToken(user),
                        () -> tokenMapper.insert(user)
                );
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("成功绑定用户: " +username).build(),false);
    }
    private void createBindError(AccessTokenPO token){
        throw new RuntimeException("您已绑定用户: " +token.getPlayer_name());
    }
    private void createAlreadyBindError(AccessTokenPO token){
        throw new RuntimeException("该用户已绑定账户: " +token.getQq_code());
    }
    private void createNotBindError(){
        {
            throw new RuntimeException("您并未绑定");
        }
    }
}
