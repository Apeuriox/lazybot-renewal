package me.aloic.lazybot.service;

import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import org.springframework.transaction.annotation.Transactional;

public interface CardService
{
    @Transactional
    String checkIn(Integer playerId);

    @Transactional
    byte[] checkIn(AccessTokenPO token);

    byte[] cardGameboy(AccessTokenPO token);

    byte[] cardGameGadget(AccessTokenPO token);
}
