package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BpCommandParameter extends LazybotCommandParameter
{
    private String playerName;
    private String mode;
    private Integer version;
    private Integer playerId;
    private Integer index;
    public BpCommandParameter(String playerName, String mode, Integer version, Integer index)
    {
        this.index=index;
        this.playerName=playerName;
        this.mode=mode;
        this.version=version;
    }
}
