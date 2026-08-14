package me.aloic.lazybot.graphics.service.impl;

import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import me.aloic.lazybot.graphics.render.SVGRenderer;
import me.aloic.lazybot.graphics.service.RasterizationService;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RasterizationServiceImpl implements RasterizationService
{
    @Resource
    private TemplateEngine templateEngine;

    @Override
    public byte[] renderToScoreDark(ScoreVO score, int hue, double saturationFactor) {
        TemplateOutput output = new StringOutput();
        Map<String,Object> params = Map.of("score",score,"hue",hue,"saturationFactor",saturationFactor);
        templateEngine.render("score_dark_v2_svg.jte", params, output);
        return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
    }

    @Override
    public byte[] renderToCardInfo(PlayerInfoVO player, int hue, double saturationFactor) {
        TemplateOutput output = new StringOutput();
        Map<String,Object> params = Map.of("player",player,"hue",hue,"saturationFactor",saturationFactor);
        templateEngine.render("card_info_short_svg.jte", params, output);
        return SVGRenderer.renderSVGDocumentToByteArray(output.toString());
    }
}
