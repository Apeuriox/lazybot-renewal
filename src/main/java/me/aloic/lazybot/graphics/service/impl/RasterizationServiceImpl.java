package me.aloic.lazybot.graphics.service.impl;

import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import jakarta.annotation.Resource;

import me.aloic.lazybot.graphics.cache.BitmapRenderCache;
import me.aloic.lazybot.graphics.cache.RenderFingerprint;
import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerDailyDelta;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.MapPerformanceAnalysis;
import me.aloic.lazybot.osu.dao.entity.vo.MapPpAnalysisView;
import me.aloic.lazybot.osu.theme.preset.CardInfoColorPalette;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RasterizationServiceImpl implements RasterizationService
{
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private BitmapRenderCache bitmapRenderCache;

    @Override
    public byte[] renderToScoreDark(ScoreVO score, int hue, double saturationFactor) {
        return bitmapRenderCache.getOrCompute(
                RenderFingerprint.of("score-dark-jte").addScore(score).key(),
                () -> {
                    TemplateOutput output = new StringOutput();
                    Map<String,Object> params = Map.of("score",score,"hue",hue,"saturationFactor",saturationFactor);
                    templateEngine.render("score_dark_v2_svg.jte", params, output);
                    return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
                });
    }

    @Override
    public byte[] renderToCardInfo(PlayerInfoVO player, PlayerDailyDelta delta, CardInfoColorPalette palette) {
        PlayerDailyDelta dailyDelta = delta == null ? PlayerDailyDelta.empty() : delta;
        CardInfoColorPalette colors = palette == null ? CardInfoColorPalette.fromRgb(null) : palette;
        return bitmapRenderCache.getOrCompute(RenderFingerprint.of("card-info-jte")
                        .add("colorSpace", "okhsl")
                        .addCardInfoPalette(colors)
                        .addDailyDelta(dailyDelta)
                        .addPlayer(player).key(),
                () -> {
                    TemplateOutput output = new StringOutput();
                    Map<String,Object> params = Map.of("player", player, "delta", dailyDelta, "palette", colors);
                    templateEngine.render("card_info_short_svg.jte", params, output);
                    return SVGRenderer.renderSVGDocumentToByteArray(output.toString(),2);
                });
    }

    @Override
    public byte[] renderToMapPpAnalysis(MapPerformanceAnalysis analysis) {
        return bitmapRenderCache.getOrCompute(
                RenderFingerprint.of("map-pp-analysis-jte").addMapAnalysis(analysis).key(),
                () -> {
                    TemplateOutput output = new StringOutput();
                    Map<String, Object> params = Map.of(
                            "model", MapPpAnalysisView.from(analysis));
                    templateEngine.render("map_pp_analysis_svg.jte", params, output);
                    return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
                });
    }
}
