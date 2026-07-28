package me.aloic.lazybot.component;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.TokenStarMoon;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.UserBindingMapper;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.osu.enums.OsuServer;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommandDatabaseProxy
{
    @Resource
    private UserBindingMapper userBindingMapper;

    @Value("${lazybot.test.identity}")
    private Long testIdentity;

    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;

    private static final Logger logger = LoggerFactory.getLogger(CommandDatabaseProxy.class);

    public UserBindingPO getUserBinding(LazybotSlashCommandEvent event)
    {
        return requireBinding(
                IdentityPlatform.QQ,
                String.valueOf(determineIdentity(event)),
                OsuServer.BANCHO,
                false);
    }

    public UserBindingPO getUserBinding(SlashCommandInteractionEvent event)
    {
        return requireBinding(
                IdentityPlatform.DISCORD,
                event.getUser().getId(),
                OsuServer.BANCHO,
                false);
    }

    public UserBindingPO getUserBinding(
            IdentityPlatform platform,
            String platformUserId,
            OsuServer server,
            boolean externalQuery)
    {
        return requireBinding(platform, platformUserId, server, externalQuery);
    }

    /**
     * @deprecated Commands should use {@link #getUserBinding(LazybotSlashCommandEvent)}.
     */
    @Deprecated
    public AccessTokenPO getAccessToken(LazybotSlashCommandEvent event)
    {
        return toLegacyAccessToken(getUserBinding(event));
    }

    /**
     * @deprecated Commands should query a UserBindingPO with an explicit platform.
     */
    @Deprecated
    public AccessTokenPO getAccessToken(Long qqCode, Boolean isExternalQuery)
    {
        UserBindingPO binding = requireBinding(
                IdentityPlatform.QQ,
                String.valueOf(qqCode),
                OsuServer.BANCHO,
                isExternalQuery);
        return binding == null ? null : toLegacyAccessToken(binding);
    }

    /**
     * Transitional adapter for existing Discord command implementations.
     */
    public UserTokenPO getDiscordBinding(SlashCommandInteractionEvent event)
    {
        return toLegacyDiscordToken(getUserBinding(event));
    }

    public TokenStarMoon getStarMoonToken(LazybotSlashCommandEvent event)
    {
        return getStarMoonToken(determineIdentity(event), false);
    }

    public TokenStarMoon getStarMoonTokenIgnoreException(LazybotSlashCommandEvent event)
    {
        try {
            return getStarMoonToken(determineIdentity(event), false);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public TokenStarMoon getStarMoonToken(Long qqCode, Boolean isExternalQuery)
    {
        UserBindingPO binding = requireBinding(
                IdentityPlatform.QQ,
                String.valueOf(qqCode),
                OsuServer.STAR_MOON,
                isExternalQuery);
        if (binding == null) {
            return null;
        }
        TokenStarMoon result = new TokenStarMoon();
        result.setId(Math.toIntExact(binding.getOsu_account_id()));
        result.setQq_code(qqCode);
        result.setStar_moon_id(binding.getPlayer_id());
        result.setStar_moon_name(binding.getPlayer_name());
        result.setDefault_mode(binding.getDefault_mode());
        result.setDefault_ruleset(binding.getDefault_subset());
        return result;
    }

    private UserBindingPO requireBinding(
            IdentityPlatform platform,
            String platformUserId,
            OsuServer server,
            boolean externalQuery)
    {
        try {
            UserBindingPO binding = userBindingMapper.selectByPlatform(
                    platform.databaseValue(),
                    platformUserId,
                    server.databaseValue());
            if (binding == null && !externalQuery) {
                String command = server == OsuServer.STAR_MOON ? "/linksm" : "/link";
                throw new LazybotRuntimeException(
                        "请先使用 " + command + " 绑定账号");
            }
            return binding;
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (MybatisPlusException e) {
            logger.error("数据库查询出错: {}", e.getMessage());
            throw new LazybotRuntimeException("数据库查询出错，详情请见log: ", e);
        }
        catch (Exception e) {
            logger.error("身份绑定查询失败", e);
            throw new LazybotRuntimeException("身份绑定查询失败，详情请见log", e);
        }
    }

    private Long determineIdentity(LazybotSlashCommandEvent event)
    {
        return testEnabled
                ? testIdentity
                : event.getMessageEvent().getSender().getUserId();
    }

    private static AccessTokenPO toLegacyAccessToken(UserBindingPO source)
    {
        AccessTokenPO target = new AccessTokenPO();
        copyBinding(source, target);
        return target;
    }

    private static UserTokenPO toLegacyDiscordToken(UserBindingPO source)
    {
        UserTokenPO target = new UserTokenPO();
        copyBinding(source, target);
        return target;
    }

    private static void copyBinding(UserBindingPO source, UserBindingPO target)
    {
        target.setId(source.getId());
        target.setPlatform_identity_id(source.getPlatform_identity_id());
        target.setPlatform(source.getPlatform());
        target.setPlatform_user_id(source.getPlatform_user_id());
        target.setOsu_account_id(source.getOsu_account_id());
        target.setPlayer_id(source.getPlayer_id());
        target.setPlayer_name(source.getPlayer_name());
        target.setServer(source.getServer());
        target.setLink_method(source.getLink_method());
        target.setVerified_at(source.getVerified_at());
        target.setDefault_mode(source.getDefault_mode());
        target.setDefault_subset(source.getDefault_subset());
        target.setPreferred_panel_version(source.getPreferred_panel_version());
        target.setEnabled(source.getEnabled());
        target.setAvatar_etag(source.getAvatar_etag());
        target.setAvatar_last_checked(source.getAvatar_last_checked());
        target.setAvatar_next_check_at(source.getAvatar_next_check_at());
    }
}
