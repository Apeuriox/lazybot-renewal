package me.aloic.lazybot.entity.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.entity.po.BadgeUserShowcasePO;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;

import java.util.List;

@AllArgsConstructor
@Data
public class ProfileInfo
{
    private PlayerInfoVO info;
    private ProfileTheme theme;
    private  List<BadgeUserShowcasePO> badges;
}
