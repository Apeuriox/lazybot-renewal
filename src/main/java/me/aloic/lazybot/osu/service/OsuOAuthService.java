package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.enums.IdentityPlatform;

public interface OsuOAuthService
{
    String createAuthorizationUrl(IdentityPlatform platform, String platformUserId);

    PlayerInfoDTO completeAuthorization(String code, String state);
}
