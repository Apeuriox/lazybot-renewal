package me.aloic.lazybot.service;

import me.aloic.lazybot.entity.command.MoelleuxCard;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.parameter.CardMoelleuxParameter;
import org.springframework.transaction.annotation.Transactional;

public interface CardService
{
    @Transactional
    String checkIn(Integer playerId);

    @Transactional
    byte[] checkIn(UserBindingPO token);

    byte[] cardGameboy(UserBindingPO token);

    byte[] cardGameGadget(UserBindingPO token);
}
