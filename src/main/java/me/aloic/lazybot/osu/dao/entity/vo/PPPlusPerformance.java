package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;

@Data
public class PPPlusPerformance
{
    private Double pp;
    private Double ppAim;
    private Double ppJumpAim;
    private Double ppFlowAim;
    private Double ppPrecision;
    private Double ppSpeed;
    private Double ppStamina;
    private Double ppAcc;
    private Double effectiveMissCount;

    @Override
    public String toString()
    {
        return "PPPlusPerformance{" +
                "pp=" + pp +
                ", ppAim=" + ppAim +
                ", ppJumpAim=" + ppJumpAim +
                ", ppFlowAim=" + ppFlowAim +
                ", ppPrecision=" + ppPrecision +
                ", ppSpeed=" + ppSpeed +
                ", ppStamina=" + ppStamina +
                ", ppAcc=" + ppAcc +
                ", effectiveMissCount=" + effectiveMissCount +
                '}';
    }
}
