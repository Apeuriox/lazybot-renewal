package me.aloic.lazybot.osu.dao.entity.dto.plus;

import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;

@Data
public class LazybotResultPlayerPerformance
{
    private Long id;
    private PPPlusPerformance performances;
}
