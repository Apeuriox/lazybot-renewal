package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class BplistParameter extends LazybotCommandParameter
{
    private Integer version;
    private Integer from;
    private Integer to;

    public BplistParameter(String playerName, String mode, Integer version, Integer from, Integer to)
    {
        if(from>to) {
            throw new IllegalArgumentException("from must be less than to");
        }
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.version=version;
        this.from=from;
        this.to=to;
    }
}
