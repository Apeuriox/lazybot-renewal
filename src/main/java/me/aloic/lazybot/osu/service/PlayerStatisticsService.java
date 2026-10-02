package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.po.PlayerStatisticsPO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerDailyDelta;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;

import java.time.LocalDate;
import java.util.List;

public interface PlayerStatisticsService
{
    void runDailySnapshot();

    List<PlayerStatisticsPO> findRange(Integer osuUserId,
                                       Integer mode,
                                       Integer subserver,
                                       LocalDate from,
                                       LocalDate toInclusive);

    PlayerStatisticsPO findLatest(Integer osuUserId, Integer mode, Integer subserver);

    PlayerDailyDelta resolveDailyDelta(PlayerInfoVO current);

    PlayerDailyDelta resolveDailyDelta(PlayerInfoVO current, Integer lookbackDays);
}
