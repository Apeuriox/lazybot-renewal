package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

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

    @Override
    public void validateParams()
    {
        if(comparePlayerName.equals(this.getPlayerName())) {
            throw new IllegalArgumentException("You cannot compare with yourself");
        }
        if (Objects.equals(comparePlayerId, this.getPlayerInfo().getId())){
            throw new IllegalArgumentException("You cannot compare with yourself");
        }
    }
}
