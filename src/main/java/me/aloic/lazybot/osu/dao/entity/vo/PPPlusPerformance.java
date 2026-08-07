package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
    private Double iffc;

    public static PPPlusPerformance initializeAsNumber(Double num)
    {
        return new PPPlusPerformance(num);
    }
    private PPPlusPerformance(Double num)
    {
        this.pp=num;
        this.ppAim=num;
        this.ppJumpAim=num;
        this.ppFlowAim=num;
        this.ppPrecision=num;
        this.ppSpeed=num;
        this.ppStamina=num;
        this.ppAcc=num;
        this.effectiveMissCount=0D;
        this.iffc=0D;
    }

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
                ", iffc=" + iffc +
                '}';
    }
}
