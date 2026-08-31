package me.aloic.lazybot.tencent;

import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.tencent.command.TencentCommandPanelRegistrar;
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
// i can give you 1,000 reasons why we should not use the official tencent api.
// the docs are so shit it just piss the fuck out of me, whoever want to give it a try go ahead
// this company is such a shame
//
// the whole part should never be enabled if we did not make a complete rewrite.
public class TencentBotRunner implements ApplicationRunner
{
    private static final Logger logger = LoggerFactory.getLogger(TencentBotRunner.class);

    private final boolean enabled;
    private final String mode;
    private final String webhookPath;
    private final TokenMonitor tokenMonitor;
    private final TencentGatewayClient gatewayClient;
    private final TencentCommandPanelRegistrar commandPanelRegistrar;

    public TencentBotRunner(
            @Value("${tencent.bot.enabled:false}") boolean enabled,
            @Value("${tencent.bot.mode:websocket}") String mode,
            @Value("${tencent.bot.webhook-path:/tencent/callback}") String webhookPath,
            TokenMonitor tokenMonitor,
            TencentGatewayClient gatewayClient,
            TencentCommandPanelRegistrar commandPanelRegistrar)
    {
        this.enabled = enabled;
        this.mode = mode;
        this.webhookPath = webhookPath;
        this.tokenMonitor = tokenMonitor;
        this.gatewayClient = gatewayClient;
        this.commandPanelRegistrar = commandPanelRegistrar;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        logger.info("Tencent 机器人启用: {}", enabled);
        if (!enabled) {
            return;
        }
        if (!tokenMonitor.hasTencentCredentials()) {
            logger.error("cant initialize tencent bot cuz credentials are null" );
            return;
        }
        try {
            tokenMonitor.refreshTencentToken();
            // they told you to manually sync your commands on the website but it ONLY CAN BE DONE in the old version of website
            // the current one, or new one, have no any text related to this, and the docs is off-maintained for 2 fucking years wtf is this
            // im disabling it anyway
//            commandPanelRegistrar.sync();
        }
        catch (Exception e) {
            logger.warn("Fuck tencent commands sync failed: {}", e.getMessage());
        }
        if ("webhook".equalsIgnoreCase(mode)) {
            logger.info("Using webhook for tencent connection: {}", webhookPath);
            return;
        }
        logger.info("正在初始化 Tencent WebSocket Gateway");
        try {
            gatewayClient.start();
        }
        catch (Exception e) {
            logger.error("Failed to start the goddamn Tencent Gateway ", e);
        }
    }
}
