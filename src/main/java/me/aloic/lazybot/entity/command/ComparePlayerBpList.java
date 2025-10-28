package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;


import java.util.List;


@Data
@AllArgsConstructor
public class ComparePlayerBpList
{
    private List<ScoreLazerDTO> scoreList;
    private PlayerInfoDTO info;
    private List<ScoreLazerDTO> compareScoreList;
    private PlayerInfoDTO compareInfo;


}
