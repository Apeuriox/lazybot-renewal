package me.aloic.lazybot.service;

import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.parameter.BadgeActionParameter;
import me.aloic.lazybot.parameter.BadgeImageParameter;
import me.aloic.lazybot.parameter.BadgeUserActionParameter;
import me.aloic.lazybot.parameter.TipsParameter;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BadgeService
{
    String addBadge(BadgeActionParameter params);

    String addBadgeImageRemote(BadgeImageParameter params);

    String addBadgeToUser(BadgeUserActionParameter param);

    String removeBadge(TipsParameter param);

    String removeBadgeFromUser(BadgeUserActionParameter param);

    String showUserAllBadgeText(Integer playerId);

    LazybotMessageWithImage showUserOwnedSingleBadge(Integer playerId, Integer index);

    String userSetShowcaseBadge(Integer lazybotId, List<Integer> indexes);

    @Transactional
    String clearUserShowcaseBadge(Integer lazybotId);
}
