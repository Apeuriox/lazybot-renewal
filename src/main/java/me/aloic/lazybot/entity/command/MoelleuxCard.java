package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoMoelleux;

import java.util.List;


@Data
@AllArgsConstructor
public class MoelleuxCard
{
    private PlayerInfoMoelleux info;
    private Integer primaryHue;
    private Boolean isLowSaturation;
    private Boolean enableWhiteMask;

    public MoelleuxCard(PlayerInfoMoelleux info,Integer hue)
    {
        this.info=info;
        this.primaryHue=hue;
    }
}
