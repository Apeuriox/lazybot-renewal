package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.osu.dao.entity.po.PlayerStatisticsPO;

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
}
