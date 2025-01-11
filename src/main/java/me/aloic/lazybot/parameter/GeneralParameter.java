package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneralParameter extends LazybotCommandParameter
{
    @Override
    public void validateParams()
    {

    }
    public GeneralParameter(String playerName, String mode)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
    }
}
