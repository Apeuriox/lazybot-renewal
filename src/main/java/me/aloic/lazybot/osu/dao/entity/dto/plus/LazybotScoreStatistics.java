package me.aloic.lazybot.osu.dao.entity.dto.plus;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;


import java.lang.reflect.Field;
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
    public void reInitialize() {
        this.count300=Optional.ofNullable(this.count300).orElse(0);
        this.count100=Optional.ofNullable(this.count100).orElse(0);
        this.count50=Optional.ofNullable(this.count50).orElse(0);
        this.count0=Optional.ofNullable(this.count0).orElse(0);
        this.countTick= Optional.ofNullable(this.countTick).orElse(0);
        this.countEnd= Optional.ofNullable(this.countEnd).orElse(0);
    }

    @Override
    public String toString()
    {
        return "LazybotScoreStatistics{" +
                "scoreId=" + scoreId +
                ", count300=" + count300 +
                ", count100=" + count100 +
                ", count50=" + count50 +
                ", count0=" + count0 +
                ", countTick=" + countTick +
                ", countEnd=" + countEnd +
                '}';
    }
}

