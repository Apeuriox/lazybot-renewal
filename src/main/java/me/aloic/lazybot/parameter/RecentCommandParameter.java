package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentCommandParameter extends LazybotCommandParameter
{
    private String playerName;
    private Integer index;
    private String mode;
    private Integer version;
    private Integer playerId;
    public RecentCommandParameter(String mode, Integer index, Integer version, String playerName)
    {
        this.mode=mode;
        this.index=index;
        this.version=version;
        this.playerName=playerName;
    }
}
