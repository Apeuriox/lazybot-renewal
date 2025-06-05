package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.command.detailedCommand.PlayRecentCommand;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ModSetting;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.ScoreStatisticsLazer;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapScore extends ScoreSequence
{
    private String bannerUrl;
    private String avatarUrl;
    private List<Mod> modJSON;
    private double iffc;
    private double starRating;
    private double bpm;

    public void setupBpm(MapScore score, BeatmapPerformance beatmap)
    {
        if (score.getModList() == null || score.getModList().isEmpty()) {
            score.setBpm(beatmap.getBpm());
            return;
        }
        double bpmMultiplier = 1.0;
        for (Mod mod : score.getModList()) {
            if (mod.getSettings()==null)
                mod.setSettings(new ModSetting());
            bpmMultiplier = switch (mod.getAcronym()) {
                case "DT", "NC" -> Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(1.5);
                case "HT", "DC" -> Optional.ofNullable(mod.getSettings().getSpeed_change()).orElse(0.75);
                default -> bpmMultiplier;
            };
        }
        score.setBpm(beatmap.getBpm() * bpmMultiplier);
    }
}
