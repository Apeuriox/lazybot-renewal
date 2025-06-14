package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScorePerformanceDTO
{
    private Long scoreId;
    private Double accuracy;
    private Double pp;
    private Double ppSpeed;
    private Double ppAim;
    private Double ppStamina;
    private Double ppJump;
    private Double ppFlow;
    private Double ppPrecision;
    private Double ppAccuracy;
    private Integer combo;
    private LocalDateTime createdAt;

    private LazybotBeatmap beatmap;
    private LazybotPlayerSummary player;
    private List<String> mods;
    private LazybotScoreStatistics statistics;
}
