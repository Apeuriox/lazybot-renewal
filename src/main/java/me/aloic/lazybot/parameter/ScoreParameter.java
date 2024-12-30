package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ScoreParameter extends LazybotCommandParameter
{
    private String modCombination;
    private Integer beatmapId;
    private Integer version;
    public ScoreParameter(String modCombination, Integer beatmapId, String mode, Integer version, String playerName)
    {
        this.modCombination=modCombination;
        this.setMode(mode);
        this.beatmapId=beatmapId;
        this.version=version;
        this.setPlayerName(playerName);
    }

    @Override
    public void validateParams() {
        if(beatmapId<=0) {
            throw new IllegalArgumentException("illegal bid: " + beatmapId);
        }
        if(modCombination.length()%2!=0) {
            throw new IllegalArgumentException("invalid mod: " + modCombination);
        }
    }
}
