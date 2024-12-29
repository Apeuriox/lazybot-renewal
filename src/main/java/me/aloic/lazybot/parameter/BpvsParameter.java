package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BpvsParameter extends LazybotCommandParameter
{
    private String comparePlayerName;
    private Integer comparePlayerId;
    public BpvsParameter(String playerName,String mode,String comparePlayerName)
    {
        this.setMode(mode);
        this.setPlayerName(playerName);
        this.comparePlayerName=comparePlayerName;
    }
}
