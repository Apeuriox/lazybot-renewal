package me.aloic.lazybot.service;

import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.parameter.*;
import org.springframework.transaction.annotation.Transactional;

public interface BadgeKeyService
{
    @Transactional
    String generateKeyForCertainBadge(BadgeKeyParameter params);

    @Transactional
    String redeemBadge(Integer lazybotId, String cdkey);

}
