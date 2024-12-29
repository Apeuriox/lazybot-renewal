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
    private String beatmapId;
    private Integer version;
    public ScoreParameter(String modCombination, String beatmapId, String mode, Integer version, String playerName)
    {
        this.modCombination=modCombination;
        this.setMode(mode);
        this.beatmapId=beatmapId;
        this.version=version;
        this.setPlayerName(playerName);
    }
}
