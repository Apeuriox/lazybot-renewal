package me.aloic.lazybot.component;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
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
        IdentityPlatform platform = event.getIdentityPlatform() != null
                ? event.getIdentityPlatform()
                : IdentityPlatform.QQ;
        return requireBinding(
                platform,
                resolvePlatformUserId(event),
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

    public UserBindingPO getQqBinding(Long qqCode, boolean externalQuery)
    {
        return requireBinding(
                IdentityPlatform.QQ,
                String.valueOf(qqCode),
                OsuServer.BANCHO,
                externalQuery);
    }

    public UserBindingPO getStarMoonBinding(LazybotSlashCommandEvent event)
    {
        return getStarMoonBinding(determineIdentity(event), false);
    }

    public UserBindingPO getStarMoonBindingIgnoreException(
            LazybotSlashCommandEvent event)
    {
        try {
            return getStarMoonBinding(determineIdentity(event), false);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public UserBindingPO getStarMoonBinding(Long qqCode, boolean externalQuery)
    {
        return requireBinding(
                IdentityPlatform.QQ,
                String.valueOf(qqCode),
                OsuServer.STAR_MOON,
                externalQuery);
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

    private String resolvePlatformUserId(LazybotSlashCommandEvent event)
    {
        if (event.getPlatformUserId() != null && !event.getPlatformUserId().isBlank()) {
            return event.getPlatformUserId();
        }
        return String.valueOf(determineIdentity(event));
    }

}
