package me.aloic.lazybot.osu.monitor;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.alibaba.fastjson2.TypeReference;
import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotWebResult;
import me.aloic.lazybot.osu.dao.entity.dto.oauth.AccessTokenDTO;
import me.aloic.lazybot.util.URLBuildUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
/**
 * 定时刷新 token：osu! 客户端凭证、PP+ 以及 Tencent 机器人 AccessToken。
 */
@Component
public class TokenMonitor
{
    @Value("${lazybot.client_id}")
    private Integer clientId;
    @Value("${lazybot.client_secret}")
    private String clientSecret;

    @Value("${lazybot.plus.client_id}")
    private Integer lazybotClientId;
    @Value("${lazybot.plus.client_password}")
    private String lazybotClientPassword;

    @Value("${tencent.bot.enabled:false}")
    private Boolean tencentEnabled;
    @Getter
    @Value("${tencent.bot.id:}")
    private String tencentAppId;
    @Getter
    @Value("${tencent.bot.secret:}")
    private String tencentClientSecret;
    @Value("${tencent.bot.token-url:https://api.bot.qq.com/app/getAppAccessToken}")
    private String tencentTokenUrl;

    private static final String TOKEN_URL = "https://osu.ppy.sh/oauth/token";

    @Value("${lazybot.plus.base_url}")
    private String PLUS_TOKEN_URL;

    private static volatile String lazybotToken;
    private static volatile String token;
    private static volatile String tencentToken;

    private static final Logger logger = LoggerFactory.getLogger(TokenMonitor.class);

    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void refreshClientToken()
    {
        try {
            logger.info("Getting Token for client");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("client_id", clientId);
            jsonObject.put("client_secret", clientSecret);
            jsonObject.put("grant_type", "client_credentials");
            jsonObject.put("scope", "public");
            Map<String, String > heads = new HashMap<>();
            heads.put("Accept", "application/json");
            heads.put("Content-Type", "application/json;charset=UTF-8");
            AccessTokenDTO tokenDTO = JSON.parseObject(HttpUtil.createPost(TOKEN_URL).addHeaders(heads).body(jsonObject.toString()).execute().body(),
                    AccessTokenDTO.class);
            logger.info("successfully created client token: {}", tokenDTO.getAccess_token());
            token= tokenDTO.getAccess_token();
        }
        catch (Exception e) {
            logger.error("{} : {}", e.getClass(), e.getMessage());
            throw new LazybotRuntimeException("刷新osu客户端Token失败");
        }
    }
    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void refreshPPPlusClientToken()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientId", lazybotClientId);
        jsonObject.put("clientSecret", lazybotClientPassword);
        try {
            LazybotWebResult<String> lazybotTokenJSON = JSON.parseObject(
                    HttpUtil.createPost(PLUS_TOKEN_URL+"/auth/token").body(jsonObject.toString()).execute().body(),
                    new TypeReference<LazybotWebResult<String>>() {}
            );
            lazybotToken= lazybotTokenJSON.getData();
            logger.info("Successfully refreshed Lazybot PP+ token");
        }
        catch (Exception e) {
            logger.error("更新PP+验证失败，请检查服务器: {} : {}", e.getClass(), e.getMessage());
//            throw new LazybotRuntimeException("更新PP+验证失败，请检查服务器");
        }
    }
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshTencentToken()
    {
        if (!Boolean.TRUE.equals(tencentEnabled) || !hasTencentCredentials()) {
            return;
        }
        try {
            logger.info("Getting Token for Tencent bot");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", tencentAppId);
            jsonObject.put("clientSecret", tencentClientSecret);
            JSONObject response = JSON.parseObject(
                    HttpUtil.createPost(tencentTokenUrl)
                            .header("Content-Type", "application/json")
                            .body(jsonObject.toString())
                            .execute()
                            .body());
            String nextToken = response == null ? null : response.getString("access_token");
            if (nextToken == null || nextToken.isBlank()) {
                throw new LazybotRuntimeException("刷新 Tencent AccessToken 失败: 响应缺少 access_token");
            }
            tencentToken = nextToken;
            logger.info("Successfully refreshed Tencent AccessToken: {}", tencentToken);
        }
        catch (Exception e) {
            logger.error("刷新 Tencent AccessToken 失败: {} : {}", e.getClass(), e.getMessage());
        }
    }

    public boolean hasTencentCredentials()
    {
        return tencentAppId != null && !tencentAppId.isBlank()
                && tencentClientSecret != null && !tencentClientSecret.isBlank();
    }

    public static String getLazybotToken() {
        if (lazybotToken == null) {
            throw new IllegalStateException("PP+获取Token未初始化！");
        }
        return lazybotToken;
    }
    public static String getToken() {
        if (token == null) {
            throw new IllegalStateException("令牌尚未获取！");
        }
        return token;
    }

    public static String getTencentToken()
    {
        if (tencentToken == null) {
            throw new IllegalStateException("Tencent AccessToken 尚未获取！");
        }
        return tencentToken;
    }
}
