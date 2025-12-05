package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoMoelleux;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;


@Data
@AllArgsConstructor
public class PerformancePlusProfile
{
    private PPPlusPerformance performance;
    private PlayerInfoVO player;

}
