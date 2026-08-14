package me.aloic.lazybot.graphics.service;

import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;

public interface RasterizationService
{
    byte[] renderToScoreDark(ScoreVO score, int hue, double saturationFactor);

    byte[] renderToCardInfo(PlayerInfoVO player, int hue, double saturationFactor);
}
