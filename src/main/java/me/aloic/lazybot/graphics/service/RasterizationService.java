package me.aloic.lazybot.graphics.service;

import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.MapPerformanceAnalysis;

public interface RasterizationService
{
    byte[] renderToScoreDark(ScoreVO score, int hue, double saturationFactor);

    byte[] renderToCardInfo(PlayerInfoVO player, int hue, double saturationFactor);

    byte[] renderToMapPpAnalysis(MapPerformanceAnalysis analysis);
}
