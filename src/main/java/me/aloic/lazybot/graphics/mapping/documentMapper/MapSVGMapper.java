package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapStatistics;
import me.aloic.lazybot.osu.utils.ColorUtil;
import me.aloic.lazybot.util.CommonTool;
import me.aloic.rosupp.GameMode;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.stream.Collectors;

public class MapSVGMapper extends LazybotSVGMapper
{


    public static Document mapBeatmapStatsToPanel(BeatmapStatistics beatmapStatistics)
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("MapStats");

        document.getElementById("playername").setTextContent(beatmapStatistics.getBeatmap().getCreator());
        document.getElementById("time").setTextContent(CommonTool.formatJsonDateToString(beatmapStatistics.getBeatmap().getLast_updated(),"yyyy-MM-dd / HH:mm:ss"));

        String modStr = null;
        if (beatmapStatistics.getImaginaryMods() != null)
            modStr = beatmapStatistics.getImaginaryMods().stream()
                    .map(Mod::getAcronym)
                    .collect(Collectors.joining());

        if (modStr == null || modStr.isEmpty())
            document.getElementById("mod").setTextContent("Nomod");
        else document.getElementById("mod").setTextContent("+" + modStr);

        document.getElementById("mode").setTextContent(beatmapStatistics.getMode().getDescribe());

        document.getElementById(beatmapStatistics.getBeatmap().getStatus()).setAttribute("opacity", "1");

        document.getElementById("map-bg").setAttributeNS(xlinkns, "xlink:href", beatmapStatistics.getMapBackgroundUrl());
        document.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", beatmapStatistics.getMapperAvatarUrl());


        document.getElementById("pp-aim").setTextContent(Math.round(beatmapStatistics.getPerformance().getAimPP()) + "pp");
        document.getElementById("pp-speed").setTextContent(Math.round(beatmapStatistics.getPerformance().getSpdPP()) + "pp");
        document.getElementById("pp-reading").setTextContent(Math.round(beatmapStatistics.getPerformance().getReadPP()) + "pp");
        document.getElementById("pp-accuracy").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPP()) + "pp");
        if (beatmapStatistics.getPerformance().getFlashlightPP()!=null && beatmapStatistics.getPerformance().getFlashlightPP()>0.5)
            document.getElementById("pp-flashlight").setTextContent(Math.round(beatmapStatistics.getPerformance().getFlashlightPP()) + "pp");
        document.getElementById("pp-distri").setTextContent(beatmapStatistics.getPpBreakdownRatioChain());

        String titleAndArtist=beatmapStatistics.getBeatmap().getArtist().concat(" - ").concat(beatmapStatistics.getBeatmap().getTitle());
        if (titleAndArtist.length()>46) titleAndArtist=titleAndArtist.substring(0,44)+"...";
        document.getElementById("artistAndTitle").setTextContent(titleAndArtist);
        document.getElementById("version").setTextContent("["+beatmapStatistics.getBeatmap().getVersion()+"]");
        document.getElementById("bid").setTextContent(String.valueOf(beatmapStatistics.getBeatmap().getBid()));

        document.getElementById("bpm").setTextContent(CommonTool.toString(beatmapStatistics.getBeatmap().getAttributes().getBpm()));
        document.getElementById("length").setTextContent(CommonTool.formatHitLength(beatmapStatistics.getBeatmap().getAttributes().getLength()));
        document.getElementById("ar").setTextContent(CommonTool.toString(beatmapStatistics.getBeatmap().getAttributes().getAr()));
        document.getElementById("od").setTextContent(CommonTool.toString(beatmapStatistics.getBeatmap().getAttributes().getOd()));
        document.getElementById("hp").setTextContent(CommonTool.toString(beatmapStatistics.getBeatmap().getAttributes().getHp()));
        document.getElementById("cs").setTextContent(CommonTool.toString(beatmapStatistics.getBeatmap().getAttributes().getCs()));

        document.getElementById("star-1").setTextContent(CommonTool.toString(beatmapStatistics.getPerformance().getStar()));
        document.getElementById("star-2").setTextContent(CommonTool.toString(beatmapStatistics.getPerformance().getStar()));
        String diffColor="#".concat(ColorUtil.getDifficultyBackgroundColor(beatmapStatistics.getPerformance().getStar()));
        String starTextColor = ColorUtil.getDifficultyColor(beatmapStatistics.getPerformance().getStar());
        document.getElementById("star-2").setAttribute("fill", starTextColor);
        document.getElementById("star-rect").setAttribute("fill", diffColor);

        var difficulty = beatmapStatistics.getBeatmap().getDifficultyAttributes();
        if (difficulty != null && difficulty.mode() == GameMode.OSU) {
                document.getElementById("bs-r-1").setTextContent(String.valueOf(beatmapStatistics.getBeatmap().getCountCircles()));
                document.getElementById("bs-r-2").setTextContent(String.valueOf(beatmapStatistics.getBeatmap().getCountSliders()));
                document.getElementById("bs-r-3").setTextContent(String.valueOf(beatmapStatistics.getBeatmap().getCountSpinners()));
                document.getElementById("bs-r-4").setTextContent(CommonTool.toString(beatmapStatistics.getBeatmap().getUser_rating()));
                document.getElementById("bs-r-5").setTextContent(beatmapStatistics.getBeatmap().getMax_combo() + "x");

                document.getElementById("da-r-1").setTextContent((int)(beatmapStatistics.getBeatmap().getLengthBonus()*100)+"%");
                document.getElementById("da-r-2").setTextContent(String.valueOf(Math.round(difficulty.aimDifficultStrainCount())));
                document.getElementById("da-r-3").setTextContent(String.valueOf(Math.round(difficulty.speedDifficultStrainCount())));
                document.getElementById("da-r-4").setTextContent(CommonTool.toString(difficulty.sliderFactor() * 100).concat("%"));
                document.getElementById("da-r-5").setTextContent(CommonTool.toString(difficulty.speedNoteCount()));
        }
        else {
            throw new LazybotRuntimeException("暂支支持其他模式");
        }
        document.getElementById("star-aim").setTextContent(CommonTool.toString(beatmapStatistics.getPerformance().getAimStar(),1));
        document.getElementById("star-speed").setTextContent(CommonTool.toString(beatmapStatistics.getPerformance().getSpeedStar(),1));
        document.getElementById("star-reading").setTextContent(CommonTool.toString(beatmapStatistics.getPerformance().getReadStar(),1));


        document.getElementById("pp-1").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPPList().get(100)) + "pp");
        document.getElementById("pp-2").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPPList().get(99)) + "pp");
        document.getElementById("pp-3").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPPList().get(98)) + "pp");
        document.getElementById("pp-4").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPPList().get(97)) + "pp");
        document.getElementById("pp-5").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPPList().get(95)) + "pp");
        document.getElementById("pp-6").setTextContent(Math.round(beatmapStatistics.getPerformance().getAccPPList().get(93)) + "pp");
        document.getElementById("pp").setTextContent(Math.round(beatmapStatistics.getPerformance().getImaginaryPP()) + "pp");
        document.getElementById("target-acc").setTextContent("("+CommonTool.toString(beatmapStatistics.getPerformance().getImaginaryAccuracy()) + "%)");


        return document;
    }

}
