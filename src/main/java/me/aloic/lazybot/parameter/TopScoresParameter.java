package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.enums.OsuMode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TopScoresParameter extends LazybotCommandParameter
{
    private OsuMode mode;
    private Integer from;
    private Integer to;
    private Integer limit;

    @Override
    void validateParams()
    {
        if(limit<=0)
        {
            throw new IllegalArgumentException("{Limit} must be greater than 0");
        }
    }
}
