package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;
import org.spring.osu.extended.rosu.JniPerformanceAttributes;

import java.util.Map;

@Data
//recalculated with 100% accuracy
public class ImaginaryPerformance
{
    private Map<Integer,Double> accPPList;
    private Double aimPP;
    private Double spdPP;
    private Double accPP;
    private Double imaginaryAccuracy;
    private Double flashlightPP;
    private Double star;
    private Double taikoDifficulty;
    private Double imaginaryPP;
    private Double aimStar;
    private Double speedStar;
}
