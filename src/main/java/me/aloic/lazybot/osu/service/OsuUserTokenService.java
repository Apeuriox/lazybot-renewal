package me.aloic.lazybot.osu.service;

import com.alibaba.fastjson2.JSON;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.oauth.AccessTokenDTO;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import me.aloic.lazybot.osu.dao.mapper.OsuOAuthCredentialMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Business-facing access to user OAuth tokens.
 *
 * <p>Public osu! API calls should continue to use the guest/client token. Use
 * this service only for endpoints that actually require the user's scopes.</p>
 */
@Service
public class OsuUserTokenService
{
    private static final URI TOKEN_URI = URI.create("https://osu.ppy.sh/oauth/token");
    private static final Duration EXPIRY_SKEW = Duration.ofMinutes(2);

    private final OsuOAuthCredentialMapper credentialMapper;
    private final HttpClient httpClient;
    private final Object[] accountLocks = new Object[64];
    private final int clientId;
    private final String clientSecret;

    public OsuUserTokenService(
            OsuOAuthCredentialMapper credentialMapper,
            @Value("${lazybot.client_id}") int clientId,
            @Value("${lazybot.client_secret}") String clientSecret)
    {
        this.credentialMapper = credentialMapper;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        for (int i = 0; i < accountLocks.length; i++)
            accountLocks[i] = new Object();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String getValidAccessToken(Long osuAccountId)
    {
        if (osuAccountId == null)
            throw new LazybotRuntimeException("osu! 账号 ID 不能为空");

        Object lock = accountLocks[
                Math.floorMod(Long.hashCode(osuAccountId), accountLocks.length)];
        synchronized (lock)
        {
            OsuOAuthCredentialPO stored = credentialMapper.selectById(osuAccountId);
            if (stored == null)
                throw new LazybotRuntimeException("该 osu! 账号没有 OAuth 凭据");

            if (isUsable(stored))
                return stored.getAccess_token();

            return refresh(stored);
        }
    }

    private String refresh(OsuOAuthCredentialPO stored)
    {
        String refreshToken = stored.getRefresh_token();
        String body = "client_id=" + clientId
                + "&client_secret=" + encode(clientSecret)
                + "&grant_type=refresh_token"
                + "&refresh_token=" + encode(refreshToken);
        HttpRequest request = HttpRequest.newBuilder(TOKEN_URI)
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        AccessTokenDTO refreshed;
        try
        {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new LazybotRuntimeException(
                        "刷新 osu! 用户令牌失败，HTTP " + response.statusCode());
            }
            refreshed = JSON.parseObject(response.body(), AccessTokenDTO.class);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new LazybotRuntimeException("刷新 osu! 用户令牌时请求被中断", e);
        }
        catch (IOException e)
        {
            throw new LazybotRuntimeException("无法连接 osu! OAuth 服务", e);
        }

        if (refreshed == null || refreshed.getAccess_token() == null
                || refreshed.getExpires_in() == null) {
            throw new LazybotRuntimeException("osu! 返回了不完整的刷新凭据");
        }

        OsuOAuthCredentialPO rotated = new OsuOAuthCredentialPO();
        rotated.setOsu_account_id(stored.getOsu_account_id());
        rotated.setAccess_token(refreshed.getAccess_token());
        rotated.setRefresh_token(
                refreshed.getRefresh_token() == null
                        ? stored.getRefresh_token()
                        : refreshed.getRefresh_token());
        rotated.setAccess_token_expires_at(
                LocalDateTime.now().plusSeconds(refreshed.getExpires_in()));
        rotated.setGranted_scopes(
                refreshed.getScope() == null
                        ? stored.getGranted_scopes()
                        : refreshed.getScope());
        rotated.setUpdated_at(LocalDateTime.now());

        if (credentialMapper.updateRotatedCredential(
                rotated, stored.getRow_version()) != 1) {
            OsuOAuthCredentialPO winner = credentialMapper.selectById(stored.getOsu_account_id());
            if (winner != null && isUsable(winner))
                return winner.getAccess_token();
            throw new LazybotRuntimeException("OAuth 凭据并发刷新失败，请重试");
        }
        return refreshed.getAccess_token();
    }

    private static boolean isUsable(OsuOAuthCredentialPO credential)
    {
        return credential.getAccess_token_expires_at() != null
                && credential.getAccess_token_expires_at()
                .isAfter(LocalDateTime.now().plus(EXPIRY_SKEW));
    }

    private static String encode(String value)
    {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
