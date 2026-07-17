package me.aloic.lazybot.component;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.TokenStarMoon;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenStarMoonMapper;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class CommandDatabaseProxy
{
    @Resource
    private TokenMapper tokenMapper;

    @Resource
    private TokenStarMoonMapper tokenStarMoonMapper;

    @Value("${lazybot.test.identity}")
    private Long testIdentity;

    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;


    private static final Logger logger = LoggerFactory.getLogger(CommandDatabaseProxy.class);

    public AccessTokenPO getAccessToken(LazybotSlashCommandEvent event)
    {
        return getAccessToken(determineIdentity(event),false);
    }

    public TokenStarMoon getStarMoonToken(LazybotSlashCommandEvent event)
    {
        return getStarMoonToken(determineIdentity(event),false);
    }
    public TokenStarMoon getStarMoonTokenIgnoreException(LazybotSlashCommandEvent event)
    {
        try{
            return getStarMoonToken(determineIdentity(event),false);
        }
        catch (Exception e)
       {
            return null;
        }
    }

    public AccessTokenPO getAccessToken(Long qqCode, Boolean isExternalQuery) {
        AccessTokenPO token = getToken(qqCode, isExternalQuery, tokenMapper::selectByQq_code);
        if (token == null) {
            throw new LazybotRuntimeException("请先使用/link 你的osu用户名 绑定osu账号，请注意不要绑定他人账户，取消绑定会删除相关组件的所有数据");
        }
        return token;
    }

    public TokenStarMoon getStarMoonToken(Long qqCode, Boolean isExternalQuery) {
        TokenStarMoon token = getToken(qqCode, isExternalQuery, tokenStarMoonMapper::selectByQq_code);
        if (token == null) {
            throw new LazybotRuntimeException("请先使用/linksm 你的StarMoon用户名 绑定star moon账号，例/link HD1");
        }
        return token;
    }
    private Long determineIdentity(LazybotSlashCommandEvent event)
    {
        if (event.getCommandContext() != null) {
            try {
                return Long.parseLong(event.getCommandContext().userId());
            }
            catch (NumberFormatException e) {
                throw new LazybotRuntimeException("当前命令用户ID不是有效数字");
            }
        }
        if (event.getMessageEvent() != null) {
            return event.getMessageEvent().getSender().getUserId();
        }
        if (testEnabled) {
            return testIdentity;
        }
        throw new LazybotRuntimeException("命令上下文中缺少用户身份");
    }

    private  <T> T getToken(Long qqCode, Boolean isExternalQuery,
                          Function<Long, T> tokenQuery) {
        try {
            T token = tokenQuery.apply(qqCode);
            if (isExternalQuery && token == null) {
                throw new LazybotRuntimeException("此用户并未绑定");
            }
            return token;
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (MybatisPlusException e) {
            logger.error("数据库查询出错: {}", e.getMessage());
            throw new LazybotRuntimeException("数据库查询出错，详情请见log: ", e);
        }
        catch (Exception e) {
            logger.error("未知错误: ", e);
            throw new LazybotRuntimeException("出现未知错误 ，详情请见log: ", e);
        }
    }


}
