package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.enums.OsuMode;

import java.util.List;

@Data
@AllArgsConstructor
public class BeatmapStatistics
{
    private BeatmapPerformance beatmap;
    private String mapperAvatarUrl;
    private String mapBackgroundUrl;
    private OsuMode mode;
    private ImaginaryPerformance performance;
    private List<Mod> imaginaryMods;
    private String ppBreakdownRatioChain;

    public BeatmapStatistics(BeatmapPerformance beatmap)
    {
        this.beatmap=beatmap;
    }

}
