package me.aloic.lazybot.parameter;
import lombok.Data;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.rosupp.AlgorithmVersion;

import java.util.List;

@Data
public abstract class LazybotCommandParameter
{
    private String playerName;
    private Integer playerId;
    private String mode;
    private List<Long> groupUserIds;
    private OsuSubruleset subRuleset;
    /** Null means use the application-wide rosu algorithm configured on the service. */
    private AlgorithmVersion algorithmVersion;

    abstract void validateParams();
}
