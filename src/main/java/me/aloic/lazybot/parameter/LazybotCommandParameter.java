package me.aloic.lazybot.parameter;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.enums.OsuSubruleset;

import java.util.List;

@Data
public abstract class LazybotCommandParameter
{
    private String playerName;
    private Integer playerId;
    private String mode;
    private List<Long> groupUserIds;
    private OsuSubruleset subRuleset;

    abstract void validateParams();
}
