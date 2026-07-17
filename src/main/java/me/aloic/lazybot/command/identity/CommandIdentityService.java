package me.aloic.lazybot.command.identity;

import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.command.core.CommandPlatform;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.enums.OsuMode;
import org.springframework.stereotype.Service;

/** Resolves platform user identities into the binding model consumed by commands. */
@Service
public class CommandIdentityService {
    private final TokenMapper tokenMapper;
    private final DiscordTokenMapper discordTokenMapper;

    public CommandIdentityService(TokenMapper tokenMapper, DiscordTokenMapper discordTokenMapper) {
        this.tokenMapper = tokenMapper;
        this.discordTokenMapper = discordTokenMapper;
    }

    public BoundOsuIdentity requireOsuIdentity(CommandContext context) {
        long platformUserId = parseNumericId(context.userId());
        try {
            return switch (context.platform()) {
                case DISCORD -> fromDiscord(platformUserId);
                case QQ, HTTP_DEV, LOCAL_TEST -> fromQq(platformUserId);
            };
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("查询用户绑定信息失败", e);
        }
    }

    private BoundOsuIdentity fromQq(long userId) {
        AccessTokenPO token = tokenMapper.selectByQq_code(userId);
        if (token == null) {
            throw new LazybotRuntimeException("请先使用/link绑定osu账号");
        }
        return new BoundOsuIdentity(
                token.getId(),
                token.getPlayer_id(),
                token.getPlayer_name(),
                token.getDefault_mode(),
                token.getPreferred_panel_version()
        );
    }

    private BoundOsuIdentity fromDiscord(long userId) {
        UserTokenPO token = discordTokenMapper.selectByDiscord(userId);
        if (token == null) {
            throw new LazybotRuntimeException("请先使用/link绑定osu账号");
        }
        return new BoundOsuIdentity(
                null,
                token.getPlayer_id(),
                token.getPlayer_name(),
                OsuMode.getMode(token.getDefault_mode()).getDescribe(),
                null
        );
    }

    private static long parseNumericId(String value) {
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            throw new LazybotRuntimeException("平台用户ID不是有效数字");
        }
    }
}
