package me.aloic.lazybot.service;

import me.aloic.lazybot.parameter.BadgeUserActionParameter;

public interface BadgeService
{
    String addBadgeToUser(BadgeUserActionParameter param);

    String removeBadge(BadgeUserActionParameter param);

    String showUserAllBadgeText(Integer playerId);
}
