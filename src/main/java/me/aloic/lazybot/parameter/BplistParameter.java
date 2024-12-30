package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class BplistParameter extends LazybotCommandParameter
{
    private Integer from;
    private Integer to;

    public BplistParameter(String playerName, String mode, Integer from, Integer to)
    {
        if(from>to) {
            throw new IllegalArgumentException("from must be less than to");
        }
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.from=from;
        this.to=to;
    }

    @Override
    public void validateParams()
    {
        if(from>100||to>100) {
            throw new IllegalArgumentException("{FROM} and {TO} must be less than 100");
        }
        if (from>=to){
            throw new IllegalArgumentException("{FROM} must be less than {TO}");
        }
        if (from<=0||to<=0) {
            throw new IllegalArgumentException("{FROM} and {TO} must be greater than 0");
        }
    }
}
