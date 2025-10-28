package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;

import java.util.List;

@AllArgsConstructor
@Data
public class UserAllScore
{
    private List<MapScore> mapScoreList;
    private BeatmapPerformance beatmapPerformance;
}
