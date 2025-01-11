package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.enums.OsuMode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TopScoresParameter extends LazybotCommandParameter
{
    private OsuMode ruleset;
    private Integer from;
    private Integer to;
    private Integer limit;

    public TopScoresParameter(String mode,Integer limit)
    {
        this.limit=limit;
        this.ruleset = OsuMode.getMode(mode);
    }

    @Override
    public void validateParams()
    {
        if(limit<=0)
        {
            throw new IllegalArgumentException("{Limit} must be greater than 0");
        }
    }
}
