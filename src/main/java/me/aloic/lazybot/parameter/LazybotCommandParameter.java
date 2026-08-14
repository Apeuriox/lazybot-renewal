package me.aloic.lazybot.parameter;
import lombok.Data;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.enums.OsuSubruleset;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
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
    // Null means use the application-wide rosu algorithm configured on the service
    private AlgorithmVersion algorithmVersion;

    public void applyAlgorithmVersion(LazybotSlashCommandEvent event)
    {
        List<String> atParameters = event.getAtParameters();
        if (atParameters == null || atParameters.isEmpty()) {
            return;
        }
        if (atParameters.size() > 1) {
            throw new LazybotRuntimeException("PP 算法版本只能指定一次");
        }

        String value = atParameters.getFirst();
        if (value.isBlank()) {
            throw new LazybotRuntimeException("PP 算法版本解析为空，是否输入了@");
        }
        this.algorithmVersion = RosuAlgorithmVersionUtil.parse(value);
    }

    abstract void validateParams();
}
