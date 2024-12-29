package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RecentParameter extends LazybotCommandParameter
{
    private Integer index;
    private Integer version;
    public RecentParameter(String mode, Integer index, Integer version, String playerName)
    {
        this.setMode(mode);
        this.index=index;
        this.version=version;
        this.setPlayerName(playerName);
    }
}
