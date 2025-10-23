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
    private TokenMapper tokenMapper;

    @Value("${lazybot.test.identity}")
    private Long testIdentity;

    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;


    private static final Logger logger = LoggerFactory.getLogger(CommandDatabaseProxy.class);

    public AccessTokenPO getAccessToken(LazybotSlashCommandEvent event)
    {
        Long identity;
        if (testEnabled) identity=testIdentity;
        else identity=event.getMessageEvent().getSender().getUserId();
        return getAccessToken(identity,false);
    }
    public AccessTokenPO getAccessToken(Long qqCode, Boolean isExternalQuery)
    {
        AccessTokenPO tokenPO;
        try {
            tokenPO = tokenMapper.selectByQq_code(qqCode);
            if (isExternalQuery){
                if (tokenPO == null) throw new LazybotRuntimeException("此用户并未绑定");
            };
            if (tokenPO == null) throw new LazybotRuntimeException("请先使用/link 你的osu用户名 绑定osu账号，请注意不要绑定他人账户，取消绑定会删除相关组件的所有数据");
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
