package me.aloic.lazybot.osu.service.ServiceImpl;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.UserResponse;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.TokenStarMoon;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.*;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.DataExtractor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiConsumer;

//im too lazy to refactor this
@Slf4j
@Service
public class UserServiceImpl implements UserService
{
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;
    @Resource
    private TokenStarMoonMapper tokenStarMoonMapper;
    @Resource
    private CardPointsMapper cardPointsMapper;
    @Resource
    private CardPointsLogMapper cardPointsLogMapper;

    @Resource
    private DataExtractor dataExtractor;



    @Override
    public void updateDefaultMode(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        if(event.getOption("mode")==null) throw new LazybotRuntimeException("请输入模式");
        OsuMode mode = OsuMode.getMode(event.getOption("mode").getAsString());
        if (mode == OsuMode.Default) throw new LazybotRuntimeException("未知的模式: " + event.getOption("mode").getAsString());
        BiConsumer<SlashCommandInteractionEvent, UserTokenPO> createBindError =  ErrorResultHandler::createBindError;
        if(event.getOption("username")==null)
            ErrorResultHandler.createParameterError(event);
        Optional.ofNullable(discordTokenMapper.selectByDiscord(event.getUser().getIdLong()))
                .ifPresentOrElse(
                        token -> discordTokenMapper.updateDefaultMode(mode.getDescribe().toLowerCase(), event.getUser().getIdLong()),
                        this::createNotBindError);
        event.getHook().sendMessage("[Lazybot] 已成功更改模式为: " +mode.getDescribe()).queue();
    }
    @Override
    public void updateDefaultMode(Bot bot, LazybotSlashCommandEvent event)
    {
        if (event.getCommandParameters()==null || event.getCommandParameters().isEmpty()) throw new LazybotRuntimeException("请输入模式");
        OsuMode mode = OsuMode.getMode(event.getCommandParameters().getFirst());
        if (mode == OsuMode.Default) throw new LazybotRuntimeException("未知的模式: " + event.getCommandParameters().getFirst());
        Optional.ofNullable(tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId()))
                .ifPresentOrElse(
                        token -> tokenMapper.updateDefaultMode(mode.getDescribe().toLowerCase(), event.getMessageEvent().getSender().getUserId()),
                        this::createNotBindError);
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] 已成功更改模式为: " +mode.getDescribe()).build(),false);
    }
    @Override
    public String updateDefaultMode(OsuSubruleset ruleset, Long qqCode)
    {
        Optional.ofNullable(tokenStarMoonMapper.selectByQq_code(qqCode))
                .ifPresentOrElse(
                        token -> tokenStarMoonMapper.updateSubRuleset(ruleset.getDescribe().toLowerCase(), qqCode),
                        this::createNotBindError);
        return "[Lazybot] 成功更新次级Ruleset至: " + ruleset.getDescribe();
    }



    @Override
    public void linkUser(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        BiConsumer<SlashCommandInteractionEvent, UserTokenPO> createBindError =  ErrorResultHandler::createBindError;
        if(event.getOption("username")==null)
            ErrorResultHandler.createParameterError(event);
        isValidUsername(event.getOption("username").getAsString());
        Optional.ofNullable(discordTokenMapper.selectByDiscord(event.getUser().getIdLong()))
                .ifPresentOrElse(
                        token -> createBindError.accept(event, token),
                        () -> insertUserToTable(event, event.getOption("username").getAsString()));
    }

    @Override
    public void linkUser(Bot bot, LazybotSlashCommandEvent event)
    {
        String username = String.join(" ", event.getCommandParameters());
        log.info("正在绑定Bancho用户");
        processLinkBancho(bot,event,username);
    }
    @Override
    public void linkStarMoon(Bot bot, LazybotSlashCommandEvent event)
    {
        String username = String.join(" ", event.getCommandParameters()).replaceAll(" ","_");
        log.info("正在绑定Star Moon用户");
        processLinkStarMoon(bot, event, username);
    }
    private void processLinkBancho(Bot bot, LazybotSlashCommandEvent event, String username)
    {
        isValidUsername(username);
        PlayerInfoDTO player = dataExtractor.extractPlayerInfoDTO(username, "osu");
        Optional.ofNullable(tokenMapper.selectByPlayerId(player.getId())).ifPresent(this::createAlreadyBindError);
        Optional.ofNullable(tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId()))
                .ifPresentOrElse(
                        this::createBindError,
                        () -> insertUserToTable(event, player, bot));
    }
    private void processLinkStarMoon(Bot bot, LazybotSlashCommandEvent event, String username)
    {
        //there can have Chinese character in username so no need to check
        UserResponse player = dataExtractor.extractPlayerStarMoon(username);
        Optional.ofNullable(tokenStarMoonMapper.selectByPlayerId(Integer.valueOf(player.getId()))).ifPresent(this::createAlreadyBindError);
        Optional.ofNullable(tokenStarMoonMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId()))
                .ifPresentOrElse(
                        this::createBindError,
                        () -> insertUserToTable(event, player, bot));
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
        event.getHook().sendMessage("[Lazybot] 已解除绑定: " +event.getOption("username").getAsString()).queue();
    }

    @Transactional
    @Override
    public void unlinkUser(Bot bot, LazybotSlashCommandEvent event)
    {
        AccessTokenPO accessTokenPO = tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId());
        Optional.ofNullable(accessTokenPO)
                .ifPresentOrElse(
                        token -> tokenMapper.deleteByQQ(event.getMessageEvent().getSender().getUserId()),
                        this::createNotBindError);
        cardPointsMapper.deleteById(accessTokenPO.getPlayer_id());
        cardPointsLogMapper.deleteById(accessTokenPO.getPlayer_id());
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] 成功解除绑定").build(),false);
    }
    private void insertUserToTable(SlashCommandInteractionEvent event, @Nonnull String username){
        PlayerInfoDTO player = dataExtractor.extractPlayerInfoDTO(username, "osu");
        UserTokenPO user = new UserTokenPO(event.getUser().getIdLong(), player.getId(), player.getUsername());
        Optional.ofNullable(discordTokenMapper.selectByPlayername(player.getUsername()))
                .ifPresentOrElse(
                        userToken -> discordTokenMapper.updateByToken(user),
                        () -> discordTokenMapper.insert(user)
                );
        event.getHook().sendMessage("[Lazybot] 成功绑定用户: " +username).queue();
    }
    private void insertUserToTable(LazybotSlashCommandEvent event, @Nonnull PlayerInfoDTO player,Bot bot){
        AccessTokenPO user = new AccessTokenPO();
        user.setPlayer_id(player.getId());
        user.setPlayer_name(player.getUsername());
        user.setDefault_mode("osu");
        user.setQq_code(event.getMessageEvent().getSender().getUserId());
        user.setValid(1);
        user.setAvatar_url(player.getAvatar_url());
        Optional.ofNullable(tokenMapper.selectByPlayerId(player.getId()))
                .ifPresentOrElse(
                        userToken -> tokenMapper.updateByToken(user),
                        () -> tokenMapper.insert(user)
                );
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] 成功绑定用户: " + player.getUsername()).build(),false);
    }

    private void insertUserToTable(LazybotSlashCommandEvent event, @Nonnull UserResponse player,Bot bot){
        TokenStarMoon user = new TokenStarMoon();
        user.setStar_moon_id(Integer.valueOf(player.getId()));
        user.setStar_moon_name(player.getName());
        user.setDefault_mode("osu");
        user.setDefault_ruleset("relax");
        user.setQq_code(event.getMessageEvent().getSender().getUserId());
        user.setCreate_time(LocalDateTime.now());
        tokenStarMoonMapper.insert(user);
        bot.sendGroupMsg(event.getMessageEvent().getGroupId(), MsgUtils.builder().text("[Lazybot] 成功绑定StarMoon用户: " + player.getName()).build(),false);
    }



    private void createBindError(AccessTokenPO token){
        throw new LazybotRuntimeException("您已绑定用户: " +token.getPlayer_name());
    }
    private void createBindError(TokenStarMoon token){
        throw new LazybotRuntimeException("您已绑定StarMoon用户: " +token.getStar_moon_name());
    }
    private void createAlreadyBindError(AccessTokenPO token){
        throw new LazybotRuntimeException("该用户已绑定账户: " +token.getQq_code());
    }
    private void createAlreadyBindError(TokenStarMoon token){
        throw new LazybotRuntimeException("该用户已绑定StarMoon账户: " +token.getQq_code());
    }
    private void createNotBindError(){
        {
            throw new LazybotRuntimeException("您并未绑定");
        }
    }
    public static void isValidUsername(String input) {
        if (input==null||input.trim().isEmpty()) throw new LazybotRuntimeException("输入用户名为空");
        if(input.trim().length()>15) throw new LazybotRuntimeException("输入用户名过长");
        if (!input.matches("^[A-Za-z0-9_\\-\\[\\] ]+$")) throw new LazybotRuntimeException("已输入的用户名含有非法字符");
    }
}
