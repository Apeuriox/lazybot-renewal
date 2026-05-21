package me.aloic.lazybot.osu.dao.entity.dto.plus;

import lombok.Data;

@Data
public class StatsCount
{
    private Integer totalPlayers;
    private Integer lastDayReallyUpdatedPlayer;
    private Integer activePlayer;
    private Integer totalRecordedScores;
    private Integer totalBeatmaps;
}
