package me.aloic.lazybot.tencent;

import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.tencent.gateway.TencentGatewayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class TencentBotRunner implements ApplicationRunner
{
    private static final Logger logger = LoggerFactory.getLogger(TencentBotRunner.class);

    private final boolean enabled;
    private final String mode;
    private final String webhookPath;
    private final TokenMonitor tokenMonitor;
    private final TencentGatewayClient gatewayClient;

    public TencentBotRunner(
            @Value("${tencent.bot.enabled:false}") boolean enabled,
            @Value("${tencent.bot.mode:websocket}") String mode,
            @Value("${tencent.bot.webhook-path:/tencent/callback}") String webhookPath,
            TokenMonitor tokenMonitor,
            TencentGatewayClient gatewayClient)
    {
        this.enabled = enabled;
        this.mode = mode;
        this.webhookPath = webhookPath;
        this.tokenMonitor = tokenMonitor;
        this.gatewayClient = gatewayClient;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        logger.info("Tencent 机器人启用: {}", enabled);
        if (!enabled) {
            return;
        }
        if (!tokenMonitor.hasTencentCredentials()) {
            logger.error("Tencent 机器人已启用，但 tencent.bot.id / tencent.bot.secret 为空");
            return;
        }
        if ("webhook".equalsIgnoreCase(mode)) {
            logger.info(
                    "Tencent 使用 Webhook 接入，回调路径 {}。开放平台仅允许 80/443/8080/8443，请用反向代理暴露 HTTPS。",
                    webhookPath);
            return;
        }
        logger.info("正在初始化 Tencent WebSocket Gateway");
        try {
            tokenMonitor.refreshTencentToken();
            gatewayClient.start();
        }
        catch (Exception e) {
            logger.error("启动 Tencent Gateway 失败。请确认已在开放平台开通群聊/单聊事件，且 AppID/AppSecret 正确", e);
        }
    }
}
