package me.aloic.lazybot.tencent.webhook;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import me.aloic.lazybot.osu.monitor.TokenMonitor;
import me.aloic.lazybot.tencent.auth.TencentEd25519;
import me.aloic.lazybot.tencent.command.TencentCommandDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.HexFormat;

@RestController
@ConditionalOnProperty(name = "tencent.bot.enabled", havingValue = "true")
public class TencentWebhookController
{
    private static final Logger logger = LoggerFactory.getLogger(TencentWebhookController.class);
    private static final int OP_DISPATCH = 0;
    private static final int OP_HTTP_CALLBACK_ACK = 12;
    private static final int OP_VALIDATION = 13;

    private final boolean enabled;
    private final String mode;
    private final TencentCommandDispatcher dispatcher;
    private final KeyPair keyPair;

    public TencentWebhookController(
            @Value("${tencent.bot.enabled:false}") boolean enabled,
            @Value("${tencent.bot.mode:websocket}") String mode,
            TokenMonitor tokenMonitor,
            TencentCommandDispatcher dispatcher)
    {
        this.enabled = enabled;
        this.mode = mode;
        this.dispatcher = dispatcher;
        KeyPair derived = null;
        if (tokenMonitor.hasTencentCredentials()) {
            try {
                derived = TencentEd25519.keyPairFromSecret(tokenMonitor.getTencentClientSecret());
            }
            catch (Exception e) {
                logger.warn("Tencent Webhook Ed25519 密钥初始化失败: {}", e.getMessage());
            }
        }
        this.keyPair = derived;
    }

    @PostMapping(
            value = "${tencent.bot.webhook-path:/tencent/callback}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> callback(HttpServletRequest request) throws Exception
    {
        if (!enabled || !"webhook".equalsIgnoreCase(mode)) {
            return ResponseEntity.notFound().build();
        }
        byte[] bodyBytes = request.getInputStream().readAllBytes();
        JSONObject payload = JSON.parseObject(new String(bodyBytes, StandardCharsets.UTF_8));
        if (payload == null) {
            return ResponseEntity.badRequest().body("{\"message\":\"empty payload\"}");
        }
        Integer op = payload.getInteger("op");
        if (op != null && op == OP_VALIDATION) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(handleValidation(payload).toJSONString());
        }
        if (!verifySignature(request, bodyBytes)) {
            logger.warn("Tencent Webhook 签名校验失败");
            return ResponseEntity.status(401).body("{\"message\":\"invalid signature\"}");
        }
        if (op != null && op == OP_DISPATCH) {
            dispatcher.dispatch(payload);
        }
        JSONObject ack = new JSONObject();
        ack.put("op", OP_HTTP_CALLBACK_ACK);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ack.toJSONString());
    }

    private JSONObject handleValidation(JSONObject payload)
    {
        JSONObject data = payload.getJSONObject("d");
        String plainToken = data == null ? null : data.getString("plain_token");
        String eventTs = data == null ? null : data.getString("event_ts");
        if (plainToken == null || eventTs == null || keyPair == null) {
            throw new IllegalStateException("Webhook 验证载荷不完整");
        }
        byte[] message = (eventTs + plainToken).getBytes(StandardCharsets.UTF_8);
        String signature = TencentEd25519.signHex(keyPair.getPrivate(), message);
        JSONObject response = new JSONObject();
        response.put("plain_token", plainToken);
        response.put("signature", signature);
        logger.info("已响应 Tencent Webhook 回调验证");
        return response;
    }

    private boolean verifySignature(HttpServletRequest request, byte[] body)
    {
        if (keyPair == null) {
            return false;
        }
        String signatureHex = request.getHeader("X-Signature-Ed25519");
        String timestamp = request.getHeader("X-Signature-Timestamp");
        if (signatureHex == null || timestamp == null) {
            return false;
        }
        try {
            byte[] signature = HexFormat.of().parseHex(signatureHex);
            byte[] message = new byte[timestamp.getBytes(StandardCharsets.UTF_8).length + body.length];
            byte[] tsBytes = timestamp.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(tsBytes, 0, message, 0, tsBytes.length);
            System.arraycopy(body, 0, message, tsBytes.length, body.length);
            return TencentEd25519.verify(keyPair.getPublic(), message, signature);
        }
        catch (Exception e) {
            return false;
        }
    }
}
