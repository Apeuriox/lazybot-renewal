package me.aloic.lazybot.graphics.service.impl;

import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;

import me.aloic.lazybot.entity.CommandHelp;
import me.aloic.lazybot.entity.CommandSummary;
import me.aloic.lazybot.graphics.cache.BitmapRenderCache;
import me.aloic.lazybot.graphics.cache.RenderFingerprint;
import me.aloic.lazybot.osu.dao.entity.view.CommandHelpPoster;
import me.aloic.lazybot.osu.dao.entity.view.HelpIndexPoster;
import me.aloic.lazybot.osu.dao.entity.view.ModWikiPoster;
import me.aloic.lazybot.osu.wiki.ModWikiCatalog;
import me.aloic.lazybot.osu.wiki.ModWikiDocument;
import me.aloic.lazybot.osu.wiki.ModModeSymbols;
import me.aloic.lazybot.graphics.render.SVGRenderer;

import java.util.List;
import java.util.Locale;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerDailyDelta;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.dao.entity.vo.MapPerformanceAnalysis;
import me.aloic.lazybot.osu.dao.entity.view.MapPerformanceAnalysisView;
import me.aloic.lazybot.osu.theme.preset.CardInfoColorPalette;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RasterizationServiceImpl implements RasterizationService
{
    private final TemplateEngine templateEngine;
    private final BitmapRenderCache bitmapRenderCache;
    private final ObjectProvider<BuildProperties> buildProperties;
    private final ModWikiCatalog modWikiCatalog;
    private final ModModeSymbols modModeSymbols;

    public RasterizationServiceImpl(TemplateEngine templateEngine,
                                    BitmapRenderCache bitmapRenderCache,
                                    ObjectProvider<BuildProperties> buildProperties,
                                    ModWikiCatalog modWikiCatalog,
                                    ModModeSymbols modModeSymbols)
    {
        this.templateEngine = templateEngine;
        this.bitmapRenderCache = bitmapRenderCache;
        this.buildProperties = buildProperties;
        this.modWikiCatalog = modWikiCatalog;
        this.modModeSymbols = modModeSymbols;
    }

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
                            "model", MapPerformanceAnalysisView.from(analysis));
                    templateEngine.render("map_pp_analysis_svg.jte", params, output);
                    return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
                });
    }

    @Override
    public byte[] renderCommandHelp(CommandHelp help)
    {
        TemplateOutput output = new StringOutput();
        templateEngine.render("single_command_help_svg.jte",
                Map.of("poster", CommandHelpPoster.from(help)), output);
        return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
    }

    @Override
    public byte[] renderHelpIndex(List<CommandSummary> summaries)
    {
        TemplateOutput output = new StringOutput();
        templateEngine.render("help_index_svg.jte",
                Map.of("poster", HelpIndexPoster.from(summaries, projectVersion())), output);
        return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
    }

    @Override
    public byte[] renderModWiki(String acronym)
    {
        String key = acronym.trim().toUpperCase(Locale.ROOT);
        return bitmapRenderCache.getOrCompute("mod-wiki:" + key, () -> {
            ModWikiDocument page = modWikiCatalog.require(key);
            TemplateOutput output = new StringOutput();
            templateEngine.render("mod_wiki_svg.jte", Map.of(
                    "poster", ModWikiPoster.from(page),
                    "modes", modModeSymbols.markup()), output);
            return SVGRenderer.renderSVGDocumentToByteArrayPNG(output.toString());
        });
    }

    private String projectVersion()
    {
        BuildProperties properties = buildProperties.getIfAvailable();
        return properties == null ? "" : properties.getVersion();
    }
}
