package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;


import java.util.Optional;

@Data
@TableName("score_statistics")
@AllArgsConstructor
@NoArgsConstructor
public class LazybotScoreStatistics
{

    @JsonIgnore
    private Long scoreId;
    private Integer count300;
    private Integer count100;
    private Integer count50;
    private Integer count0;
    private Integer countTick;
    private Integer countEnd;

    public LazybotScoreStatistics(ScoreStatisticsLazer statisticsLazer, Long scoreId) {
        this.scoreId=scoreId;
        this.count300=Optional.ofNullable(statisticsLazer.getGreat()).orElse(0);
        this.count100=Optional.ofNullable(statisticsLazer.getOk()).orElse(0);
        this.count50=Optional.ofNullable(statisticsLazer.getMeh()).orElse(0);
        this.count0=Optional.ofNullable(statisticsLazer.getMiss()).orElse(0);
        this.countTick= Optional.ofNullable(statisticsLazer.getLarge_tick_hit()).orElse(0);
        this.countEnd= Optional.ofNullable(statisticsLazer.getSlider_tail_hit()).orElse(0);
    }
}
