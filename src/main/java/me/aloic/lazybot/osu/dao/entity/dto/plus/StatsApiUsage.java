package me.aloic.lazybot.osu.dao.entity.dto.plus;

import lombok.Data;

@Data
public class StatsApiUsage
{
    private String apiName;
    private Integer totalCount;
    private Integer successCount;
    private Integer errorCount;
    private Long totalLatency;
    private Double avgLatency;
}
