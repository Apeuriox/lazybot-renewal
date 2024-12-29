package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BplistCommandParameter extends LazybotCommandParameter
{
    private String playerName;
    private Integer playerId;
    private String mode;
    private Integer version;
    private Integer from;
    private Integer to;

    public BplistCommandParameter(String playerName, String mode, Integer version, Integer from, Integer to)
    {
        if(from>to) {
            throw new IllegalArgumentException("from must be less than to");
        }
        this.playerName=playerName;
        this.mode=mode;
        this.version=version;
        this.from=from;
        this.to=to;
    }
}
