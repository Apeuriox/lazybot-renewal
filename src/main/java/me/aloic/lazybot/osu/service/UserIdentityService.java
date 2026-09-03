package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.po.OAuthLinkSessionPO;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import me.aloic.lazybot.osu.dao.entity.po.PlatformIdentityPO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.enums.IdentityPlatform;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.OsuServer;

/**
 * Platform identity, osu! account binding, OAuth link sessions, and user token refresh.
 */
public interface UserIdentityService
{
    UserBindingPO findBinding(
            IdentityPlatform platform, String platformUserId, OsuServer server);

    UserBindingPO bindManual(IdentityPlatform platform,
                             String platformUserId,
                             OsuServer server,
                             Integer osuUserId,
                             String username);

    UserBindingPO bindManual(IdentityPlatform platform,
                             String platformUserId,
                             OsuServer server,
                             Integer osuUserId,
                             String username,
                             OsuMode defaultMode);

    PlatformIdentityPO ensurePlatformIdentity(
            IdentityPlatform platform, String platformUserId);

    /**
     * Completes a proven OAuth binding.
     *
     * <p>An OAuth account can replace a manual account owned by the same Lazybot
     * user. A manually claimed target osu! account can also be transferred to the
     * authenticated user. An existing OAuth-verified binding is never overwritten
     * implicitly.</p>
     */
    void bindOAuth(
            Long platformIdentityId,
            OsuServer server,
            Integer osuUserId,
            String username,
            OsuMode defaultMode,
            OsuOAuthCredentialPO credential);

    void unlink(IdentityPlatform platform, String platformUserId, OsuServer server);

    void updateDefaultMode(
            IdentityPlatform platform, String platformUserId, OsuMode mode);

    void updateDefaultSubset(
            IdentityPlatform platform, String platformUserId, String subset);

    void updatePreferredPanel(
            IdentityPlatform platform, String platformUserId, Integer version);

    PlatformIdentityPO requirePlatformIdentity(
            IdentityPlatform platform, String platformUserId);

    boolean hasAnyOsuAccount(Integer lazybotUserId);

    void createLinkSession(Long platformIdentityId, String state);

    OAuthLinkSessionPO consumeLinkSession(String state);

    /**
     * Returns a usable user access token, refreshing it when close to expiry.
     *
     * <p>Public osu! API calls should continue to use the guest/client token.
     * Use this only for endpoints that actually require the user's scopes.</p>
     */
    String getValidAccessToken(Long osuAccountId);
}
