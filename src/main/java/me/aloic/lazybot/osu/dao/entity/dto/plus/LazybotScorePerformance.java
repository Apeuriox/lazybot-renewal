package me.aloic.lazybot.osu.dao.entity.dto.plus;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class LazybotScorePerformance
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
}
