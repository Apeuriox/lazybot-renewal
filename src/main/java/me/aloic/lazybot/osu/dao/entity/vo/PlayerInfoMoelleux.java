package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayerInfoMoelleux
{
    private PlayerInfoVO info;
    private List<ScoreVO> bps;
    private PPPlusPerformance plus;
}
