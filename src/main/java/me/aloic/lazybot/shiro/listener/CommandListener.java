package me.aloic.lazybot.shiro.listener;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import jakarta.annotation.Resource;
import me.aloic.lazybot.shiro.utils.MessageDeduplicator;
import me.aloic.lazybot.shiro.utils.MessageEventFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Shiro
@Component
@SuppressWarnings("unused")
public class CommandListener
{
    private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);

    @Resource
    private MessageDeduplicator messageDeduplicator;

    @Resource
    private MessageEventFactory factory;

    @GroupMessageHandler
    //we dont need it anymore cuz Shiro have a Async thread pool here (and it is virtual wow).
//    @Async("virtualThreadExecutor")
    public void onSlashCommandInteraction(Bot bot, GroupMessageEvent event) {
        logger.trace("收到消息[{}] -> {}", event.getGroupId(), ShiroUtils.unescape(event.getRawMessage()));
        var nowTime = System.currentTimeMillis();
        if (event.getTime() < 1e10) {
            nowTime /= 1000;
        }
        // 对于超过 25 秒的消息直接舍弃，避免重新登录后集中处理积压命令。
        if (nowTime - event.getTime() > 25) return;
        messageDeduplicator.replicateCheck(bot, factory.setupSlashCommandEvent(event));
    }


}


