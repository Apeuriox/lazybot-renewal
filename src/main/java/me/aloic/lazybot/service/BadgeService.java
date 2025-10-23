package me.aloic.lazybot.service;

import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.parameter.BadgeActionParameter;
import me.aloic.lazybot.parameter.BadgeImageParameter;
import me.aloic.lazybot.parameter.BadgeUserActionParameter;
import me.aloic.lazybot.parameter.TipsParameter;

public interface BadgeService
{
    String addBadge(BadgeActionParameter params);

    String addBadgeImageRemote(BadgeImageParameter params);

    String addBadgeToUser(BadgeUserActionParameter param);

    String removeBadge(TipsParameter param);

    String removeBadgeFromUser(BadgeUserActionParameter param);

    String showUserAllBadgeText(Integer playerId);

    LazybotMessageWithImage showUserOwnedSingleBadge(Integer playerId, Integer index);
}
