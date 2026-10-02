package me.aloic.lazybot.osu.service.ServiceImpl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.oauth.AccessTokenDTO;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUserPO;
import me.aloic.lazybot.osu.dao.entity.po.OAuthLinkSessionPO;
import me.aloic.lazybot.osu.dao.entity.po.OsuAccountPO;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import me.aloic.lazybot.osu.dao.entity.po.PlatformIdentityPO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.dao.mapper.LazybotUserMapper;
import me.aloic.lazybot.osu.dao.mapper.OAuthLinkSessionMapper;
import me.aloic.lazybot.osu.dao.mapper.OsuAccountMapper;
import me.aloic.lazybot.osu.dao.mapper.OsuOAuthCredentialMapper;
import me.aloic.lazybot.osu.dao.mapper.PlatformIdentityMapper;
import me.aloic.lazybot.osu.dao.mapper.UserBindingMapper;
import me.aloic.lazybot.osu.enums.AccountLinkMethod;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuServer;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.service.UserIdentityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserIdentityServiceImpl implements UserIdentityService
{
    private static final URI TOKEN_URI = URI.create("https://osu.ppy.sh/oauth/token");
    private static final Duration EXPIRY_SKEW = Duration.ofMinutes(2);
    private static final Duration LINK_SESSION_TTL = Duration.ofMinutes(10);

    private final LazybotUserMapper lazybotUserMapper;
    private final PlatformIdentityMapper platformIdentityMapper;
    private final OsuAccountMapper osuAccountMapper;
    private final OsuOAuthCredentialMapper oauthCredentialMapper;
    private final UserBindingMapper userBindingMapper;
    private final OAuthLinkSessionMapper sessionMapper;
    private final HttpClient httpClient;
    private final Object[] accountLocks = new Object[64];
    private final int clientId;
    private final String clientSecret;

    public UserIdentityServiceImpl(LazybotUserMapper lazybotUserMapper,
                                   PlatformIdentityMapper platformIdentityMapper,
                                   OsuAccountMapper osuAccountMapper,
                                   OsuOAuthCredentialMapper oauthCredentialMapper,
                                   UserBindingMapper userBindingMapper,
                                   OAuthLinkSessionMapper sessionMapper,
                                   @Value("${lazybot.client_id}") int clientId,
                                   @Value("${lazybot.client_secret}") String clientSecret)
    {
        this.lazybotUserMapper = lazybotUserMapper;
        this.platformIdentityMapper = platformIdentityMapper;
        this.osuAccountMapper = osuAccountMapper;
        this.oauthCredentialMapper = oauthCredentialMapper;
        this.userBindingMapper = userBindingMapper;
        this.sessionMapper = sessionMapper;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        for (int i = 0; i < accountLocks.length; i++)
            accountLocks[i] = new Object();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public UserBindingPO findBinding(
            IdentityPlatform platform, String platformUserId, OsuServer server)
    {
        return userBindingMapper.selectByPlatform(
                platform.databaseValue(),
                platformUserId,
                server.databaseValue());
    }

    @Override
    @Transactional
    public UserBindingPO bindManual(IdentityPlatform platform,
                                    String platformUserId,
                                    OsuServer server,
                                    Integer osuUserId,
                                    String username)
    {
        return bindManual(platform, platformUserId, server, osuUserId, username, null);
    }

    @Override
    @Transactional
    public UserBindingPO bindManual(IdentityPlatform platform,
                                    String platformUserId,
                                    OsuServer server,
                                    Integer osuUserId,
                                    String username,
                                    OsuMode defaultMode)
    {
        if (osuAccountMapper.selectByServerIdentity(server.databaseValue(), osuUserId) != null) {
            throw new LazybotRuntimeException("用户 " + server.databaseValue() + " 已被绑定");
        }

        PlatformIdentityPO platformIdentity = platformIdentityMapper.selectByPlatformIdentity(platform.databaseValue(), platformUserId);
        Integer lazybotUserId;
        if (platformIdentity == null) {
            lazybotUserId = createLazybotUser();
            platformIdentity = new PlatformIdentityPO();
            platformIdentity.setLazybot_user_id(lazybotUserId);
            platformIdentity.setPlatform(platform.databaseValue());
            platformIdentity.setPlatform_user_id(platformUserId);
            platformIdentity.setCreated_at(LocalDateTime.now());
            platformIdentityMapper.insert(platformIdentity);
        }
        else {
            lazybotUserId = platformIdentity.getLazybot_user_id();
            OsuAccountPO current = osuAccountMapper.selectByUserAndServer(
                    lazybotUserId, server.databaseValue());
            if (current != null) {
                throw new LazybotRuntimeException(
                        "您已绑定用户: " + current.getUsername_cache());
            }
        }

        OsuAccountPO account = new OsuAccountPO();
        account.setLazybot_user_id(lazybotUserId);
        account.setServer(server.databaseValue());
        account.setOsu_user_id(osuUserId);
        account.setUsername_cache(username);
        account.setLink_method(AccountLinkMethod.MANUAL.databaseValue());
        account.setCreated_at(LocalDateTime.now());
        account.setUpdated_at(LocalDateTime.now());
        try {
            osuAccountMapper.insert(account);
        }
        catch (DuplicateKeyException e) {
            throw new LazybotRuntimeException("该 " + server.databaseValue() + " 用户已被绑定", e);
        }

        synchronizeDefaultMode(lazybotUserId, defaultMode);
        return findBinding(platform, platformUserId, server);
    }

    @Override
    @Transactional
    public PlatformIdentityPO ensurePlatformIdentity(
            IdentityPlatform platform, String platformUserId)
    {
        PlatformIdentityPO existing = platformIdentityMapper.selectByPlatformIdentity(
                platform.databaseValue(), platformUserId);
        if (existing != null)
            return existing;

        Integer userId = createLazybotUser();
        PlatformIdentityPO identity = new PlatformIdentityPO();
        identity.setLazybot_user_id(userId);
        identity.setPlatform(platform.databaseValue());
        identity.setPlatform_user_id(platformUserId);
        identity.setCreated_at(LocalDateTime.now());
        platformIdentityMapper.insert(identity);
        return identity;
    }

    @Override
    @Transactional
    public void bindOAuth(Long platformIdentityId,
                          OsuServer server,
                          Integer osuUserId,
                          String username,
                          OsuMode defaultMode,
                          OsuOAuthCredentialPO credential)
    {
        PlatformIdentityPO identity =
                platformIdentityMapper.selectByIdForUpdate(platformIdentityId);
        if (identity == null)
            throw new LazybotRuntimeException("发起 OAuth 绑定的平台身份不存在");

        Integer userId = identity.getLazybot_user_id();
        OsuAccountPO current = osuAccountMapper.selectByUserAndServerForUpdate(
                userId, server.databaseValue());
        OsuAccountPO target = osuAccountMapper.selectByServerIdentityForUpdate(
                server.databaseValue(), osuUserId);

        if (target != null && !target.getLazybot_user_id().equals(userId))
        {
            if (AccountLinkMethod.OAUTH.databaseValue().equals(target.getLink_method()))
            {
                if (current != null && !current.getId().equals(target.getId())) {
                    rejectReplacingVerifiedAccount(current);
                }

                userId = target.getLazybot_user_id();
                platformIdentityMapper.reassignToUser(platformIdentityId, userId);
            }
            else
            {
                if (current != null && !current.getId().equals(target.getId())) {
                    rejectReplacingVerifiedAccount(current);
                    deleteAccountExplicitly(current);
                }

                target.setLazybot_user_id(userId);
            }
        }
        else if (target == null)
        {
            if (current != null)
            {
                rejectReplacingVerifiedAccount(current);
                deleteAccountExplicitly(current);
            }

            target = new OsuAccountPO();
            target.setLazybot_user_id(userId);
            target.setServer(server.databaseValue());
            target.setOsu_user_id(osuUserId);
            target.setCreated_at(LocalDateTime.now());
        }

        target.setUsername_cache(username);
        target.setLink_method(AccountLinkMethod.OAUTH.databaseValue());
        target.setVerified_at(LocalDateTime.now());
        target.setUpdated_at(LocalDateTime.now());
        if (target.getId() == null)
            osuAccountMapper.insert(target);
        else
            osuAccountMapper.updateById(target);

        credential.setOsu_account_id(target.getId());
        OsuOAuthCredentialPO existingCredential =
                oauthCredentialMapper.selectByAccountIdForUpdate(target.getId());
        if (existingCredential == null) {
            oauthCredentialMapper.insert(credential);
        }
        else {
            credential.setCreated_at(existingCredential.getCreated_at());
            if (oauthCredentialMapper.updateRotatedCredential(
                    credential, existingCredential.getRow_version()) != 1) {
                throw new LazybotRuntimeException("OAuth 凭据并发更新失败，请重试");
            }
        }

        synchronizeDefaultMode(userId, defaultMode);
    }

    @Override
    @Transactional
    public void unlink(IdentityPlatform platform, String platformUserId, OsuServer server)
    {
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        OsuAccountPO account = osuAccountMapper.selectByUserAndServerForUpdate(
                identity.getLazybot_user_id(), server.databaseValue());
        if (account == null) {
            throw new LazybotRuntimeException("您并未绑定");
        }

        deleteAccountExplicitly(account);
    }

    @Override
    public void updateDefaultMode(
            IdentityPlatform platform, String platformUserId, OsuMode mode)
    {
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        lazybotUserMapper.updateDefaultMode(
                identity.getLazybot_user_id(), mode.getDescribe());
    }

    @Override
    public void updateDefaultSubset(
            IdentityPlatform platform, String platformUserId, String subset)
    {
        if (subset == null || subset.isBlank()) {
            throw new LazybotRuntimeException("默认子模式不能为空");
        }
        String normalizedSubset = OsuSubruleset.getRuleset(subset).getDescribe();
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        lazybotUserMapper.updateDefaultSubset(
                identity.getLazybot_user_id(), normalizedSubset);
    }

    @Override
    public void updatePreferredPanel(
            IdentityPlatform platform, String platformUserId, Integer version)
    {
        PlatformIdentityPO identity = requirePlatformIdentity(platform, platformUserId);
        lazybotUserMapper.updatePreferredPanel(identity.getLazybot_user_id(), version);
    }

    @Override
    public PlatformIdentityPO requirePlatformIdentity(
            IdentityPlatform platform, String platformUserId)
    {
        PlatformIdentityPO identity = platformIdentityMapper.selectByPlatformIdentity(
                platform.databaseValue(), platformUserId);
        if (identity == null) {
            throw new LazybotRuntimeException("您并未绑定");
        }
        return identity;
    }

    @Override
    public boolean hasAnyOsuAccount(Integer lazybotUserId)
    {
        return osuAccountMapper.selectCount(new LambdaQueryWrapper<OsuAccountPO>()
                        .eq(OsuAccountPO::getLazybot_user_id, lazybotUserId)) > 0;
    }

    @Override
    @Transactional
    public void createLinkSession(Long platformIdentityId, String state)
    {
        sessionMapper.invalidateOutstanding(platformIdentityId, LocalDateTime.now());
        OAuthLinkSessionPO session = new OAuthLinkSessionPO();
        session.setState_hash(hash(state));
        session.setPlatform_identity_id(platformIdentityId);
        session.setExpires_at(LocalDateTime.now().plus(LINK_SESSION_TTL));
        sessionMapper.insert(session);
    }

    @Override
    @Transactional
    public OAuthLinkSessionPO consumeLinkSession(String state)
    {
        OAuthLinkSessionPO session = sessionMapper.selectByStateHashForUpdate(hash(state));
        LocalDateTime now = LocalDateTime.now();
        if (session == null || session.getConsumed_at() != null) {
            throw new LazybotRuntimeException("OAuth 链接无效或已经使用");
        }
        if (session.getExpires_at() == null || !session.getExpires_at().isAfter(now)) {
            throw new LazybotRuntimeException("OAuth 链接已过期，请重新生成");
        }
        if (sessionMapper.markConsumed(session.getId(), now) != 1) {
            throw new LazybotRuntimeException("OAuth 链接已经使用");
        }
        return session;
    }

    @Override
    public String getValidAccessToken(Long osuAccountId)
    {
        if (osuAccountId == null)
            throw new LazybotRuntimeException("osu! 账号 ID 不能为空");

        Object lock = accountLocks[Math.floorMod(Long.hashCode(osuAccountId), accountLocks.length)];
        synchronized (lock)
        {
            OsuOAuthCredentialPO stored = oauthCredentialMapper.selectById(osuAccountId);
            if (stored == null)
                throw new LazybotRuntimeException("该 osu! 账号没有 OAuth 凭据");

            if (isUsable(stored))
                return stored.getAccess_token();

            return refresh(stored);
        }
    }

    private Integer createLazybotUser()
    {
        LazybotUserPO user = new LazybotUserPO();
        user.setDefault_mode(OsuMode.Osu.getDescribe());
        user.setDefault_subset("relax");
        user.setEnabled(true);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        lazybotUserMapper.insert(user);
        return user.getId();
    }

    private void synchronizeDefaultMode(Integer lazybotUserId, OsuMode defaultMode)
    {
        if (defaultMode != null && defaultMode != OsuMode.Default) {
            lazybotUserMapper.updateDefaultMode(
                    lazybotUserId, defaultMode.getDescribe());
        }
    }

    private void rejectReplacingVerifiedAccount(OsuAccountPO account)
    {
        if (AccountLinkMethod.OAUTH.databaseValue().equals(account.getLink_method())) {
            throw new LazybotRuntimeException(
                    "当前平台身份已绑定另一个经过 OAuth 验证的 osu! 账号，请先解除绑定");
        }
    }

    private void deleteAccountExplicitly(OsuAccountPO account)
    {
        // No database cascade: the optional credential references osu_account.
        oauthCredentialMapper.deleteById(account.getId());
        osuAccountMapper.deleteById(account.getId());
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

        if (oauthCredentialMapper.updateRotatedCredential(
                rotated, stored.getRow_version()) != 1) {
            OsuOAuthCredentialPO winner = oauthCredentialMapper.selectById(stored.getOsu_account_id());
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

    private static byte[] hash(String state)
    {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(state.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM 不支持 SHA-256", e);
        }
    }

    private static String encode(String value)
    {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
