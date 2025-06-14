package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import lombok.Data;

@Data
public class LazybotWebPlayerPerformance
{
    private Integer code;
    private LazybotResultPlayerPerformance data;
    private String msg;
}
