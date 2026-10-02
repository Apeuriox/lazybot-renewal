package me.aloic.lazybot.osu.service.ServiceImpl;

import com.alibaba.fastjson2.JSON;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.oauth.AccessTokenDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.OAuthLinkSessionPO;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import me.aloic.lazybot.osu.dao.entity.po.PlatformIdentityPO;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuServer;
import me.aloic.lazybot.osu.service.OsuOAuthService;
import me.aloic.lazybot.osu.service.UserIdentityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class OsuOAuthServiceImpl implements OsuOAuthService
{
    private static final String AUTHORIZE_URL = "https://osu.ppy.sh/oauth/authorize";
    private static final String TOKEN_URL = "https://osu.ppy.sh/oauth/token";
    private static final String CURRENT_USER_URL = "https://osu.ppy.sh/api/v2/me";

    private final UserIdentityService identityService;
    private final HttpClient httpClient;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String scopes;

    public OsuOAuthServiceImpl(UserIdentityService identityService,
                               @Value("${lazybot.client_id}") int clientId,
                               @Value("${lazybot.client_secret}") String clientSecret,
                               @Value("${lazybot.oauth.redirect-uri:http://localhost:9001/oauth/osu/callback}")
                               String redirectUri,
                               @Value("${lazybot.oauth.scopes:identify public}") String scopes)
    {
        this.identityService = identityService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    // currently not enabled cuz we need a web page
    @Override
    @Transactional
    public String createAuthorizationUrl(IdentityPlatform platform, String platformUserId)
    {
        PlatformIdentityPO identity = identityService.ensurePlatformIdentity(platform, platformUserId);

        byte[] stateBytes = new byte[32];
        secureRandom.nextBytes(stateBytes);
        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);
        identityService.createLinkSession(identity.getId(), state);

        return AUTHORIZE_URL
                + "?client_id=" + clientId
                + "&redirect_uri=" + encode(redirectUri)
                + "&response_type=code"
                + "&scope=" + encode(scopes)
                + "&state=" + encode(state);
    }

    @Override
    public PlayerInfoDTO completeAuthorization(String code, String state)
    {
        if (code == null || code.isBlank() || state == null || state.isBlank()) {
            throw new LazybotRuntimeException("OAuth 回调缺少 code 或 state");
        }

        OAuthLinkSessionPO session = identityService.consumeLinkSession(state);
        AccessTokenDTO token = exchangeCode(code);
        if (token == null || token.getAccess_token() == null
                || token.getRefresh_token() == null || token.getExpires_in() == null) {
            throw new LazybotRuntimeException("osu! OAuth 返回了不完整的凭据");
        }
        PlayerInfoDTO player = requestCurrentUser(token.getAccess_token());

        OsuOAuthCredentialPO credential = new OsuOAuthCredentialPO();
        credential.setAccess_token(token.getAccess_token());
        credential.setRefresh_token(token.getRefresh_token());
        credential.setAccess_token_expires_at(
                LocalDateTime.now().plusSeconds(token.getExpires_in()));
        credential.setGranted_scopes(
                token.getScope() == null ? scopes : token.getScope());
        credential.setRow_version(0L);
        credential.setCreated_at(LocalDateTime.now());
        credential.setUpdated_at(LocalDateTime.now());

        identityService.bindOAuth(
                session.getPlatform_identity_id(),
                OsuServer.BANCHO,
                player.getId(),
                player.getUsername(),
                OsuMode.getMode(player.getPlaymode()),
                credential);
        return player;
    }

    private AccessTokenDTO exchangeCode(String code)
    {
        String body = "client_id=" + clientId
                + "&client_secret=" + encode(clientSecret)
                + "&code=" + encode(code)
                + "&grant_type=authorization_code"
                + "&redirect_uri=" + encode(redirectUri);
        return executeJson(
                HttpRequest.newBuilder(URI.create(TOKEN_URL))
                        .timeout(Duration.ofSeconds(20))
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                AccessTokenDTO.class);
    }

    private PlayerInfoDTO requestCurrentUser(String accessToken)
    {
        return executeJson(
                HttpRequest.newBuilder(URI.create(CURRENT_USER_URL))
                        .timeout(Duration.ofSeconds(20))
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build(),
                PlayerInfoDTO.class);
    }

    private <T> T executeJson(HttpRequest request, Class<T> type)
    {
        try
        {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new LazybotRuntimeException(
                        "osu! OAuth 请求失败，HTTP " + response.statusCode());
            }
            return JSON.parseObject(response.body(), type);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new LazybotRuntimeException("osu! OAuth 请求被中断", e);
        }
        catch (IOException e)
        {
            throw new LazybotRuntimeException("无法连接 osu! OAuth 服务", e);
        }
    }

    private static String encode(String value)
    {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
