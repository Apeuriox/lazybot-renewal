package me.aloic.lazybot.Service.Impl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.Service.CardService;
import me.aloic.lazybot.entity.po.CardUserPointsPO;
import me.aloic.lazybot.osu.dao.mapper.CardPointsMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class CardServiceImpl implements CardService
{
    @Resource
    private CardPointsMapper cardPointsMapper;

    public String checkIn(Long playerId)
    {
        CardUserPointsPO playerStats = cardPointsMapper.selectById(playerId.intValue());
        int coinThisTime = new Random().nextInt(30)+20;
        if (playerStats == null) {
            cardPointsMapper.insert(new CardUserPointsPO(playerId.intValue(), coinThisTime, coinThisTime, 0, 1, 1, LocalDateTime.now()));
            return "[Lazybot] 签到成功，本次获得"+coinThisTime+"Lazycoin;";
        }
        if (playerStats.getLast_signin_time().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            return "[Lazybot] 今天已经签到过了哦";
        }
        return null;
    }
}
