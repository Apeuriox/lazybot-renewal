package me.aloic.lazybot.graphics.service;

import me.aloic.lazybot.osu.dao.entity.vo.PlayerDailyDelta;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.MapPerformanceAnalysis;
import me.aloic.lazybot.osu.theme.preset.CardInfoColorPalette;

public interface RasterizationService
{
    byte[] renderToScoreDark(ScoreVO score, int hue, double saturationFactor);

    byte[] renderToCardInfo(PlayerInfoVO player, PlayerDailyDelta delta, CardInfoColorPalette palette);

    byte[] renderToMapPpAnalysis(MapPerformanceAnalysis analysis);
}
