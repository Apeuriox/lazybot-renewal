package me.aloic.lazybot.service;

import me.aloic.lazybot.parameter.BadgeUserActionParameter;
import me.aloic.lazybot.parameter.TipsParameter;

public interface BadgeService
{
    String addBadgeToUser(BadgeUserActionParameter param);

    String removeBadge(TipsParameter param);

    String removeBadgeFromUser(BadgeUserActionParameter param);

    String showUserAllBadgeText(Integer playerId);
}
