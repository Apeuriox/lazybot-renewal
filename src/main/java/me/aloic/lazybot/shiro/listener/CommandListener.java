package me.aloic.lazybot.shiro.listener;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.ReplyEnum;
import me.aloic.lazybot.discord.config.DiscordBotRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Shiro
@Component
public class CommandListener extends BotPlugin
{
    private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
    @Override
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        logger.info("recive private message: {}", event.getMessage());
        if ("ping".equals(event.getMessage())) {
            // 构建消息
            String sendMsg = MsgUtils.builder()
                    .text("pong!")
                    .build();
            // 发送私聊消息
            bot.sendPrivateMsg(event.getUserId(), sendMsg, false);
        }
        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        if ("ping".equals(event.getMessage())) {
            // 构建消息
            String sendMsg = MsgUtils.builder()
                    .text("pong!")
                    .build();
            // 发送群消息
            bot.sendGroupMsg(event.getGroupId(), sendMsg, false);
        }
        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }
}


