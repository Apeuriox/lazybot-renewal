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
    public TodaybpParameter(Integer maxDays, String playerName)
    {
        this.maxDays=maxDays;
        this.setPlayerName(playerName);
    }
}
