package me.aloic.lazybot.component;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.annotation.Resource;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.DiscordTokenMapper;
import me.aloic.lazybot.osu.dao.mapper.TokenMapper;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandDatabaseProxy
{
    @Resource
    private DiscordTokenMapper discordTokenMapper;
    @Resource
    private TokenMapper tokenMapper;
    @Resource
    private TokenMonitor tokenMonitor;

    @Value("${lazybot.test.identity}")
    private Long testIdentity;

    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;


    private static final Logger logger = LoggerFactory.getLogger(CommandDatabaseProxy.class);

    public AccessTokenPO getAccessToken(LazybotSlashCommandEvent event)
    {
        AccessTokenPO accessToken, tokenPO;
        try {
            if (testEnabled) tokenPO = tokenMapper.selectByQq_code(testIdentity);
            else tokenPO = tokenMapper.selectByQq_code(event.getMessageEvent().getSender().getUserId());

            if (tokenPO == null) throw new LazybotRuntimeException("请先使用/link 你的osu用户名 绑定osu账号");
            accessToken= tokenMapper.selectByQq_code(0L);
            if(accessToken == null) {
                tokenMonitor.refreshClientToken();
                throw new LazybotRuntimeException("Osu api token失效，正在重获取");
            }
            tokenPO.setAccess_token(accessToken.getAccess_token());
            return tokenPO;
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (MybatisPlusException e) {
            logger.error("数据库查询出错: {}", e.getMessage());
            throw new LazybotRuntimeException("数据库查询出错，详情请见log");
        }
        catch (Exception e) {
            logger.error("未知错误: {}", e.getMessage());
            throw new LazybotRuntimeException("出现未知错误 ，详情请见log");
        }
    }
}
