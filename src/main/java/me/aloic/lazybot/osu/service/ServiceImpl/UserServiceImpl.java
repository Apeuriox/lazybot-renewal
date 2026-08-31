package me.aloic.lazybot.osu.service.ServiceImpl;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.discord.util.ErrorResultHandler;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.dto.starmoon.UserResponse;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuServer;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.enums.ScorePanelType;
import me.aloic.lazybot.osu.service.UserIdentityService;
import me.aloic.lazybot.osu.service.UserService;
import me.aloic.lazybot.osu.service.OsuOAuthService;
import me.aloic.lazybot.parameter.UpdatePanelVersionParameter;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.DataExtractor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService
{
    @Resource
    private UserIdentityService identityService;

    @Resource
    private DataExtractor dataExtractor;
    @Resource
    private OsuOAuthService oauthService;

    @Override
    public void updateDefaultSubset(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        if (event.getOption("mode") == null) {
            throw new LazybotRuntimeException("请输入模式");
        }
        OsuMode mode = OsuMode.getMode(event.getOption("mode").getAsString());
        identityService.updateDefaultMode(
                IdentityPlatform.DISCORD, event.getUser().getId(), mode);
        event.getHook().sendMessage(
                "[Lazybot] 已成功更改模式为: " + mode.getDescribe()).queue();
    }

    @Override
    public void updateDefaultSubset(Bot bot, LazybotSlashCommandEvent event)
    {
        if (event.getCommandParameters() == null || event.getCommandParameters().isEmpty()) {
            throw new LazybotRuntimeException("请输入模式");
        }
        OsuMode mode = OsuMode.getMode(event.getCommandParameters().getFirst());
        identityService.updateDefaultMode(
                IdentityPlatform.QQ,
                String.valueOf(event.getMessageEvent().getSender().getUserId()),
                mode);
        bot.sendGroupMsg(
                event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(
                        "[Lazybot] 已成功更改模式为: " + mode.getDescribe()).build(),
                false);
    }

    @Override
    public String updateDefaultSubset(OsuSubruleset ruleset, Long qqCode)
    {
        identityService.updateDefaultSubset(
                IdentityPlatform.QQ,
                String.valueOf(qqCode),
                ruleset.getDescribe());
        return "[Lazybot] 成功更新次级Ruleset至: " + ruleset.getDescribe();
    }

    @Override
    public void linkUser(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        if (event.getOption("username") == null) {
            ErrorResultHandler.createParameterError(event);
            return;
        }

        String username = event.getOption("username").getAsString();
        if ("oauth".equalsIgnoreCase(username)) {
            String url = oauthService.createAuthorizationUrl(
                    IdentityPlatform.DISCORD, event.getUser().getId());
            event.getHook().sendMessage(
                    "[Lazybot] 请在 10 分钟内打开此链接并登录 osu! 完成授权：\n" + url)
                    .setEphemeral(true)
                    .queue();
            return;
        }
        isValidUsername(username);
        PlayerInfoDTO player = dataExtractor.extractPlayerInfoDTO(username);
        OsuMode defaultMode = OsuMode.getMode(player.getPlaymode());
        checkUserBindability(player);
        identityService.bindManual(
                IdentityPlatform.DISCORD,
                event.getUser().getId(),
                OsuServer.BANCHO,
                player.getId(),
                player.getUsername(),
                defaultMode);
        event.getHook().sendMessage(
                "[Lazybot] 成功绑定用户: " + player.getUsername()).queue();
    }

    @Override
    public void linkUser(Bot bot, LazybotSlashCommandEvent event)
    {
        String username = String.join(" ", event.getCommandParameters());
        if ("oauth".equalsIgnoreCase(username)) {
            long userId = event.getMessageEvent().getSender().getUserId();
            String url = oauthService.createAuthorizationUrl(
                    IdentityPlatform.QQ, String.valueOf(userId));
            bot.sendPrivateMsg(
                    userId,
                    MsgUtils.builder().text(
                            "[Lazybot] 请在 10 分钟内打开此链接并登录 osu! 完成授权：\n"
                                    + url).build(),
                    false);
            bot.sendGroupMsg(
                    event.getMessageEvent().getGroupId(),
                    MsgUtils.builder().text("[Lazybot] OAuth 授权链接已通过私聊发送").build(),
                    false);
            return;
        }
        isValidUsername(username);
        log.info("正在手动绑定 Bancho 用户");

        PlayerInfoDTO player = dataExtractor.extractPlayerInfoDTO(username);
        OsuMode defaultMode = OsuMode.getMode(player.getPlaymode());
        checkUserBindability(player);
        identityService.bindManual(
                IdentityPlatform.QQ,
                String.valueOf(event.getMessageEvent().getSender().getUserId()),
                OsuServer.BANCHO,
                player.getId(),
                player.getUsername(),
                defaultMode);
        bot.sendGroupMsg(
                event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(
                        "[Lazybot] 成功绑定用户: " + player.getUsername()).build(),
                false);
    }

    @Override
    public void linkStarMoon(Bot bot, LazybotSlashCommandEvent event)
    {
        String username = String.join(" ", event.getCommandParameters())
                .replace(" ", "_");
        log.info("正在手动绑定 StarMoon 用户");

        UserResponse player = dataExtractor.extractPlayerStarMoon(username);
        identityService.bindManual(
                IdentityPlatform.QQ,
                String.valueOf(event.getMessageEvent().getSender().getUserId()),
                OsuServer.STAR_MOON,
                Integer.valueOf(player.getId()),
                player.getName());
        bot.sendGroupMsg(
                event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text(
                        "[Lazybot] 成功绑定StarMoon用户: " + player.getName()).build(),
                false);
    }

    @Override
    public void unlinkUser(SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        identityService.unlink(
                IdentityPlatform.DISCORD,
                event.getUser().getId(),
                OsuServer.BANCHO);
        event.getHook().sendMessage("[Lazybot] 已解除 osu! 账号绑定").queue();
    }

    @Override
    public void unlinkUser(Bot bot, LazybotSlashCommandEvent event)
    {
        identityService.unlink(
                IdentityPlatform.QQ,
                String.valueOf(event.getMessageEvent().getSender().getUserId()),
                OsuServer.BANCHO);
        bot.sendGroupMsg(
                event.getMessageEvent().getGroupId(),
                MsgUtils.builder().text("[Lazybot] 成功解除绑定").build(),
                false);
    }

    public static void isValidUsername(String input)
    {
        if (input == null || input.trim().isEmpty()) {
            throw new LazybotRuntimeException("输入用户名为空");
        }
        if (input.trim().length() > 15) {
            throw new LazybotRuntimeException("输入用户名过长");
        }
        if (!input.matches("^[A-Za-z0-9_\\-\\[\\] ]+$")) {
            throw new LazybotRuntimeException("已输入的用户名含有非法字符");
        }
    }

    private void checkUserBindability(PlayerInfoDTO player)
    {
        if (player.getStatistics() == null
                || player.getStatistics().getGlobal_rank() == null) {
            throw new LazybotRuntimeException(
                    "用户不活跃，无法实际确定绑定可行性，请在游玩1pc后重试");
        }
        if (player.getId() == 2) {
            throw new LazybotRuntimeException(
                    "操作已终止，只见后台传回一段话：您哪位？");
        }
        if (player.getStatistics().getGlobal_rank() > 1000) {
            return;
        }
        if (player.getCountry_code().equalsIgnoreCase("CN")
                || player.getCountry_code().equalsIgnoreCase("HK")
                || player.getCountry_code().equalsIgnoreCase("TW")
                || player.getCountry_code().equalsIgnoreCase("MO")) {
            return;
        }
        throw new LazybotRuntimeException(
                "当前绑定高概率为冒用，已拒绝请求。若确为本人请使用 OAuth 绑定");
    }

    @Override
    public String updatedUserPreferredPanelVersion(UpdatePanelVersionParameter params)
    {
        ScorePanelType panel = ScorePanelType.getPanelType(
                String.valueOf(params.getPlayerName()));
        identityService.updatePreferredPanel(
                IdentityPlatform.QQ,
                String.valueOf(params.getQqCode()),
                panel.getInternalVersionCode());
        return "[Lazybot] 成功将默认渲染切换为 " + panel.getFullName();
    }
}
