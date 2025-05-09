package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceVO
{
    private Double ifFc;
    private Double currentPP;
    private Map<Integer,Double> accPPList;
    private Double aimPP;
    private Double spdPP;
    private Double accPP;
    private Double flashlightPP;
    private Double aimPPMax;
    private Double spdPPMax;
    private Double accPPMax;
    private Double flashlightPPMax;
    private Double star;
    private Double taikoDifficulty;

    @Override
    public String toString()
    {
        return "PerformanceVO{" +
                "ifFc=" + ifFc +
                ", currentPP=" + currentPP +
                ", accPPList=" + accPPList +
                ", aimPP=" + aimPP +
                ", spdPP=" + spdPP +
                ", accPP=" + accPP +
                ", flashlightPP=" + flashlightPP +
                ", aimPPMax=" + aimPPMax +
                ", spdPPMax=" + spdPPMax +
                ", accPPMax=" + accPPMax +
                ", flashlightPPMax=" + flashlightPPMax +
                ", star=" + star +
                ", taikoDifficulty=" + taikoDifficulty +
                '}';
    }
}
