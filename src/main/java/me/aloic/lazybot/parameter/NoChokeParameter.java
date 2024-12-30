package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoChokeParameter extends LazybotCommandParameter
{
    @Override
    public void validateParams()
    {

    }
    public NoChokeParameter(String playerName,String mode)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
    }
}
