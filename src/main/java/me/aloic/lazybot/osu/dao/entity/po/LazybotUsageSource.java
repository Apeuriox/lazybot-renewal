package me.aloic.lazybot.osu.dao.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LazybotUsageSource
{
    private int index;
    private String name;
    private int count;
}
