package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
//recalculated with 100% accuracy
public class ImaginaryPerformance
{
    private Map<Integer,Double> accPPList;
    private Double aimPP;
    private Double spdPP;
    private Double accPP;
    private Double readPP;
    private Double imaginaryAccuracy;
    private Double flashlightPP;
    private Double star;
    private Double imaginaryPP;
    private Double aimStar;
    private Double speedStar;
    private Double readStar;

    public ImaginaryPerformance(double acc)
    {
        this.imaginaryAccuracy=acc;
    }
}
