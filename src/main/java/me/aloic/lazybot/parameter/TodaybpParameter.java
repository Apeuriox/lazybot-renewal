package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class TodaybpParameter extends LazybotCommandParameter
{
    private Integer maxDays;
    public TodaybpParameter(String playerName,String mode,Integer maxDays)
    {
        this.maxDays=maxDays;
        this.setPlayerName(playerName);
        this.setMode(mode);
    }

    @Override
    public void validateParams()
    {
        if(maxDays<=0) {
            throw new IllegalArgumentException("Days must be greater than 0");
        }
        if(maxDays>=10000)
        {
            throw new IllegalArgumentException("Days is longer than total osu game lifespan");
        }
    }
}
