package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreCommandParameter extends LazybotCommandParameter
{
    private String modCombination;
    private String playerName;
    private String beatmapId;
    private String mode;
    private Integer version;
    private Integer playerId;
    public ScoreCommandParameter(String modCombination, String beatmapId, String mode, Integer version, String playerName)
    {
        this.modCombination=modCombination;
        this.mode=mode;
        this.beatmapId=beatmapId;
        this.version=version;
        this.playerName=playerName;
    }
}
