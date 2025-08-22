package me.aloic.lazybot.Service.Impl;

import jakarta.annotation.Resource;
import me.aloic.lazybot.Service.CardService;
import me.aloic.lazybot.entity.po.CardUserPointsLogPO;
import me.aloic.lazybot.entity.po.CardUserPointsPO;
import me.aloic.lazybot.entity.vo.CheckInStats;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.documentMapper.CardCheckInSVGMapper;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.mapper.CardPointsLogMapper;
import me.aloic.lazybot.osu.dao.mapper.CardPointsMapper;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
public class CardServiceImpl implements CardService
{
    @Resource
    private CardPointsMapper cardPointsMapper;
    @Resource
    private CardPointsLogMapper cardPointsLogMapper;

    @Transactional
    @Override
    public String checkIn(Integer playerId)
    {
        CardUserPointsPO playerStats = cardPointsMapper.selectById(playerId);
        int coinThisTime = new Random().nextInt(30)+20;
        if (playerStats == null) {
            cardPointsMapper.insert(new CardUserPointsPO(playerId, coinThisTime, coinThisTime, 0, 1, 1, LocalDateTime.now()));
            cardPointsLogMapper.insert(new CardUserPointsLogPO(playerId,coinThisTime,"CheckIn",LocalDateTime.now()));
            return "[Lazybot] 签到成功，本次获得 "+coinThisTime+" Lazycoin，请注意若绑定不是本人请重新绑定，切换绑定会重置所有数据";
        }
        if (playerStats.getLast_signin_time().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            return "[Lazybot] 今天已经签到过了哦";
        }
        else if(ChronoUnit.DAYS.between(playerStats.getLast_signin_time(), LocalDateTime.now()) <= 1)
        {
            return UpdatePlayerCoin(playerId, playerStats, coinThisTime, playerStats.getContinuous_check_time()+1);
        }
        else {
            return UpdatePlayerCoin(playerId, playerStats, coinThisTime,1);
        }
    }
    @Transactional
    @Override
    public byte[] checkIn(AccessTokenPO token)
    {
        CardUserPointsPO playerStats = cardPointsMapper.selectById(token.getPlayer_id());
        int coinThisTime = new Random().nextInt(30)+20;
        if (playerStats == null) {
            cardPointsMapper.insert(new CardUserPointsPO(token.getPlayer_id(), coinThisTime, coinThisTime, 0, 1, 1, LocalDateTime.now()));
            cardPointsLogMapper.insert(new CardUserPointsLogPO(token.getPlayer_id(),coinThisTime,"CheckIn",LocalDateTime.now()));
            String avatarUrl = AssertDownloadUtil.avatarAbsolutePath(token.getPlayer_id());
            return SVGRenderer.renderSVGDocumentToByteArray(
                    CardCheckInSVGMapper.mapCheckinStatsToCard(
                            new CheckInStats(1,1, coinThisTime, coinThisTime, coinThisTime, token.getPlayer_name(),avatarUrl)
                    ));
        }
        if (playerStats.getLast_signin_time().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            throw new LazybotRuntimeException("[Lazybot] 今天已经签到过了哦");
        }
        else if(ChronoUnit.DAYS.between(playerStats.getLast_signin_time(), LocalDateTime.now()) <= 1)
        {
            return UpdatePlayerCoin(token.getPlayer_id(), playerStats,token, coinThisTime, playerStats.getContinuous_check_time()+1);
        }
        else {
            return UpdatePlayerCoin(token.getPlayer_id(), playerStats,token, coinThisTime,1);
        }
    }

    @Transactional
    @NotNull
    protected String UpdatePlayerCoin(Integer playerId, CardUserPointsPO playerStats, int coinThisTime, int continuous)
    {
        cardPointsMapper.updateStatsCheck(playerStats.getPoints()+coinThisTime,
                LocalDateTime.now(),
                playerStats.getTotal_history_points()+coinThisTime,
                playerStats.getAccumulated_check_time()+1,
                continuous,
                playerId);
        cardPointsLogMapper.insert(new CardUserPointsLogPO(playerId,coinThisTime,"CheckIn",LocalDateTime.now()));
        return "[Lazybot] 签到成功，您已连续签到" + continuous + "天，本次获得 "+coinThisTime+" Lazycoin";
    }

    @Transactional
    @NotNull
    protected byte[] UpdatePlayerCoin(Integer playerId, CardUserPointsPO playerStats,AccessTokenPO token ,int coinThisTime, int continuous)
    {
        cardPointsMapper.updateStatsCheck(playerStats.getPoints()+coinThisTime,
                LocalDateTime.now(),
                playerStats.getTotal_history_points()+coinThisTime,
                playerStats.getAccumulated_check_time()+1,
                continuous,
                playerId);
        cardPointsLogMapper.insert(new CardUserPointsLogPO(playerId,coinThisTime,"CheckIn",LocalDateTime.now()));
        CheckInStats stats =  new CheckInStats(playerStats,coinThisTime,token);
        stats.setContinuousCheckIns(continuous);
        return SVGRenderer.renderSVGDocumentToByteArray(
            CardCheckInSVGMapper.mapCheckinStatsToCard(stats
            )
        );
    }
}
