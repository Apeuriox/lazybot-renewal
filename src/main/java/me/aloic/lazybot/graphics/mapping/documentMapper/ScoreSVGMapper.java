package me.aloic.lazybot.graphics.mapping.documentMapper;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.ModColor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.RankColor;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

@Slf4j
public class ScoreSVGMapper extends LazybotSVGMapper
{
    private static Document mapScoreToScorePanelWhite(ScoreVO targetScore)
    {
        long startingTime = System.currentTimeMillis();
        try
        {
            Document doc = SVGTemplateLoader.loadSVGTemplate("scorePanelSimplifiedV4");
            //此图片元素对应替换玩家的头像以及Beatmap的背景图
            NodeList imageElements = doc.getElementsByTagName("image");

            for (int i = 0; i < imageElements.getLength(); i++)
            {
                Element imageElement = (Element) imageElements.item(i);
                String id = imageElement.getAttribute("id");
                switch (id)
                {
                    case "avatar":
                        if (targetScore.getAvatarUrl() != null)
                        {
                            imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getAvatarUrl());
                        }
                        break;
                    case "mapBg-right":
                        if (targetScore.getBeatmap().getBgUrl() != null)
                        {
                            imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getBeatmap().getBgUrl());
                        }
                        break;
                }
            }

            doc.getElementById("playerName").setTextContent(targetScore.getUser_name());
            if (targetScore.getBeatmap().getArtist().length() < 24)
            {
                doc.getElementById("artistName").setTextContent(targetScore.getBeatmap().getArtist());
            }
            else
            {
                doc.getElementById("artistName").setTextContent(targetScore.getBeatmap().getArtist().substring(0, 22).concat("..."));
            }
            doc.getElementById("mapperName").setTextContent(targetScore.getBeatmap().getCreator());
            doc.getElementById("beatmapId").setTextContent(String.valueOf(targetScore.getBeatmap().getBid()));
            doc.getElementById("starRating").setTextContent(CommonTool.toString(targetScore.getBeatmap().getDifficult_rating()));
            doc.getElementById("roughTime").setTextContent(CommonTool.timestampSpilt(targetScore.getCreate_at())[0]);
            doc.getElementById("preciseTime").setTextContent(CommonTool.timestampSpilt(targetScore.getCreate_at())[1]);
            if (targetScore.getPp() != null)
            {
                doc.getElementById("totalPP").setTextContent(CommonTool.toString(Math.round(targetScore.getPp())).concat(" PP"));
            }
            else if (targetScore.getPpDetailsLocal().getCurrentPP() != null)
            {
                doc.getElementById("totalPP").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getCurrentPP())).concat(" PP"));
            }
            else
            {
                doc.getElementById("totalPP").setTextContent("- PP");
            }
            if (targetScore.getPpDetailsLocal() != null)
            {
                doc.getElementById("aimPPtotal").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAimPPMax())));
                doc.getElementById("spdPPtotal").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getSpdPPMax())));
                doc.getElementById("accPPtotal").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPMax())));

                doc.getElementById("aimPPget").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAimPP())));
                doc.getElementById("accPPget").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPP())));
                doc.getElementById("spdPPget").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getSpdPP())));
                doc.getElementById("iffc").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getIfFc())).concat(" PP"));

                doc.getElementById("95%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(95))).concat(" PP"));
                doc.getElementById("97%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(97))).concat(" PP"));
                doc.getElementById("98%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(98))).concat(" PP"));
                doc.getElementById("99%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(99))).concat(" PP"));
                doc.getElementById("100%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(100))).concat(" PP"));
            }


            doc.getElementById("accuracy").setTextContent(CommonTool.toString(targetScore.getAccuracy() * 100).concat("%"));
            if (targetScore.getBeatmap().getMax_combo() != null)
            {
                doc.getElementById("comboStatus").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x/")
                        .concat(CommonTool.toString(targetScore.getBeatmap().getMax_combo())
                                .concat("x")).concat(" (").
                        concat(CommonTool.toString(((double) targetScore.getMaxCombo() / (double) targetScore.getBeatmap().getMax_combo()) * 100.0).concat("%)")));
            }
            else
            {
                doc.getElementById("comboStatus").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x"));
            }
            doc.getElementById("mode").setTextContent(OsuMode.getMode(targetScore.getMode()).getDescribe().concat("!"));
            doc.getElementById("score").setTextContent(CommonTool.transformNumber(String.valueOf(targetScore.getScore())));
            if (targetScore.getBeatmap().getVersion().length() < 24)
            {
                doc.getElementById("version").setTextContent(targetScore.getBeatmap().getVersion());
            }
            else
            {
                doc.getElementById("version").setTextContent(targetScore.getBeatmap().getVersion().substring(0, 23).concat("..."));
            }
            doc.getElementById("100Count").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
            doc.getElementById("300Count").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
            doc.getElementById("50Count").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
            doc.getElementById("missCount").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));
            doc.getElementById("100CountShadow").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
            doc.getElementById("300CountShadow").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
            doc.getElementById("50CountShadow").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
            doc.getElementById("missCountShadow").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));

            doc.getElementById("bpm").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getBpm()));
            doc.getElementById("length").setTextContent(CommonTool.formatHitLength(targetScore.getBeatmap().getAttributes().getLength()));
            doc.getElementById("AR").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getAr()));
            doc.getElementById("OD").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getOd()));
            doc.getElementById("HP").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getHp()));
            doc.getElementById("CS").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getCs()));
            doc.getElementById("mods").setTextContent(CommonTool.modArrayToString(targetScore.getMods()));
            if (targetScore.getBeatmap().getTitle().length() < 24) {
                doc.getElementById("songTitle1").setTextContent(targetScore.getBeatmap().getTitle());
            }
            else {
                doc.getElementById("songTitle1").setTextContent(targetScore.getBeatmap().getTitle().substring(0, 23).concat("..."));
            }
            doc.getElementById(targetScore.getBeatmap().getStatus() + "Status").setAttribute("opacity", "1");
            doc.getElementById(targetScore.getBeatmap().getStatus() + "BG").setAttribute("opacity", "1");
            Element grade = doc.getElementById("grade");
            doc.getElementById("gradeShadow").setTextContent(targetScore.getRank());
            grade.setTextContent(targetScore.getRank());
            grade.setAttribute("fill", RankColor.fromString(targetScore.getRank()).getDarkRankColorHEX());
            log.info("Batik Util Cost (white Mode):{}ms", System.currentTimeMillis() - startingTime);
            return doc;
        } catch (Exception e)
        {
            log.error("Error while generating score panel: {}", e);
            throw new LazybotRuntimeException("[Lazybot] 亮色模式Score panel生成失败");
        }
    }
    private static Document mapScoreToScorePanelMaterial(ScoreVO targetScore,int[] primaryColor)
    {
        try{
            List<Double> hsl = CommonTool.rgbToHslDetailed(primaryColor);
            String color =String.format("hsl(%.0f, %.0f%%, %.0f%%)", hsl.get(0),  hsl.get(1) * 100,  (hsl.get(2) * 100)+20>94?94:(hsl.get(2) * 100)+20);
            Document document = SVGTemplateLoader.loadSVGTemplate("ScorePanelMaterialDesign-Reranged");
            Element svgRoot = document.getDocumentElement();
            document.getElementById("color-0").setAttribute("fill",color);
            document.getElementById("color-1").setAttribute("fill",color);
            document.getElementById("color-2").setAttribute("fill",color);
            document.getElementById("color-3").setAttribute("fill",color);
            document.getElementById("color-4").setAttribute("fill",color);
            document.getElementById("color-5").setAttribute("fill",color);
            document.getElementById("color-6").setAttribute("fill",color);
            document.getElementById("color-7").setAttribute("fill",color);
            document.getElementById("color-8").setAttribute("fill",color);
            document.getElementById("color-9").setAttribute("fill",color);
            document.getElementById("color-10").setAttribute("fill",color);
            document.getElementById("mapBg").setAttributeNS(xlinkns, "xlink:href", targetScore.getBeatmap().getBgUrl());
            document.getElementById("mapBg-mask").setAttributeNS(xlinkns, "xlink:href", targetScore.getBeatmap().getBgUrl());
            document.getElementById("playername").setTextContent(targetScore.getUser_name());
            document.getElementById("achievedTime").setTextContent(CommonTool.timestampSpilt(targetScore.getCreate_at())[0]);
            document.getElementById("title").setTextContent(targetScore.getBeatmap().getTitle());
            document.getElementById("artist").setTextContent(targetScore.getBeatmap().getArtist());
            document.getElementById("mapper").setTextContent(targetScore.getBeatmap().getCreator());
            document.getElementById("version").setTextContent(targetScore.getBeatmap().getVersion());
            document.getElementById("genre").setTextContent(targetScore.getBeatmap().getGenre());
            document.getElementById("language").setTextContent(targetScore.getBeatmap().getLanguage());
            document.getElementById("starRating").setTextContent(CommonTool.toString(targetScore.getBeatmap().getDifficult_rating()));
            document.getElementById("status").setTextContent(targetScore.getBeatmap().getStatus().toUpperCase());
            document.getElementById("bid").setTextContent(String.valueOf(targetScore.getBeatmap().getBid()));
            document.getElementById("sid").setTextContent(String.valueOf(targetScore.getBeatmap().getSid()));
            document.getElementById("bpm").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getBpm()));
            document.getElementById("length").setTextContent(CommonTool.formatHitLength(targetScore.getBeatmap().getAttributes().getLength()));
            document.getElementById("ar").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getAr()));
            document.getElementById("od").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getOd()));
            document.getElementById("hp").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getHp()));
            document.getElementById("cs").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getCs()));
            document.getElementById("ok").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
            document.getElementById("great").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
            document.getElementById("meh").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
            document.getElementById("miss").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));
            document.getElementById("rank").setTextContent(targetScore.getRank().toUpperCase());
            if (targetScore.getPpDetailsLocal().getCurrentPP() != null) {
                document.getElementById("pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getCurrentPP())));
            }
            document.getElementById("accuracy").setTextContent(CommonTool.toString(targetScore.getAccuracy() * 100).concat("%"));
            document.getElementById("combo").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x/")
                    .concat(CommonTool.toString(targetScore.getBeatmap().getMax_combo())
                            .concat("x")).concat(" (").
                    concat(CommonTool.toString(((double) targetScore.getMaxCombo() / (double) targetScore.getBeatmap().getMax_combo()) * 100.0).concat("%)")));

            if (targetScore.getPpDetailsLocal() != null)
            {
                document.getElementById("aimPPAll").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAimPPMax())));
                document.getElementById("spdPPAll").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getSpdPPMax())));
                document.getElementById("accPPAll").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPMax())));

                document.getElementById("aimPPGet").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAimPP())));
                document.getElementById("accPPGet").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPP())));
                document.getElementById("spdPPGet").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getSpdPP())));
                document.getElementById("iffc").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getIfFc())));

                document.getElementById("95%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(95))));
                document.getElementById("97%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(97))));
                document.getElementById("98%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(98))));
                document.getElementById("99%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(99))));
                document.getElementById("100%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(100))));
            }
            document.getElementById(OsuMode.getMode(targetScore.getMode()).getDescribe()).setAttribute("class", "cls-23");
            return document;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new LazybotRuntimeException("生成Material Score图像时出错");
        }
    }
    private static Document mapScoreToScorePanelDark(ScoreVO targetScore,int[] primaryColor)
    {
        long startingTime = System.currentTimeMillis();

        try {
            int hue=CommonTool.rgbToHue(primaryColor);
            Document doc = SVGTemplateLoader.loadSVGTemplate("scorePanelDarkmode_customize");
            //此图片元素对应替换玩家的头像以及Beatmap的背景图
            NodeList imageElements = doc.getElementsByTagName("image");

            for (int i = 0; i < imageElements.getLength(); i++) {
                Element imageElement = (Element) imageElements.item(i);
                String id = imageElement.getAttribute("id");
                switch (id) {
                    case "avatar":
                        if (targetScore.getAvatarUrl() != null)
                            imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getAvatarUrl());
                        break;
                    case "mapBg-right":
                        if (targetScore.getBeatmap().getBgUrl() != null)
                            imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getBeatmap().getBgUrl());
                        break;
                }
            }

            if(targetScore.getIsLazer()) {
                doc.getElementById("lazer-label").setAttribute("opacity", "1");
            }

            doc.getElementById("playerName").setTextContent(targetScore.getUser_name());
            if (targetScore.getBeatmap().getArtist().length() < 20) {
                doc.getElementById("artist").setTextContent(targetScore.getBeatmap().getArtist());
            }
            else {
                doc.getElementById("artist").setTextContent(targetScore.getBeatmap().getArtist().substring(0, 19).concat("..."));
            }
            doc.getElementById("mapper").setTextContent(targetScore.getBeatmap().getCreator());
            doc.getElementById("bid").setTextContent(String.valueOf(targetScore.getBeatmap().getBid()));
            doc.getElementById("starRating").setTextContent(CommonTool.toString(targetScore.getBeatmap().getDifficult_rating()));

            if (targetScore.getBeatmap().getDifficult_rating() < 7.0)
            {
                if (targetScore.getBeatmap().getDifficult_rating() % 1.0 > 0.5)
                {
                    doc.getElementById("starRating").setAttribute("fill", "#fed867");
                    doc.getElementById("starRatingStar").setAttribute("fill", "#fed867");
                }
                else
                {
                    doc.getElementById("starRating").setAttribute("fill", "#1c1719");
                    doc.getElementById("starRatingStar").setAttribute("fill", "#1c1719");
                }
            }
            else if (targetScore.getBeatmap().getDifficult_rating() > 10)
            {
                doc.getElementById("starRating").setAttribute("fill", "#fed867");
                doc.getElementById("starRatingStar").setAttribute("fill", "#fed867");
                doc.getElementById("starRatingBG").setAttribute("width", "150");
            }
            else
            {
                doc.getElementById("starRating").setAttribute("fill", "#fed867");
                doc.getElementById("starRatingStar").setAttribute("fill", "#fed867");
            }


            doc.getElementById("roughTime").setTextContent(CommonTool.timestampSpilt(targetScore.getCreate_at())[0]);
            doc.getElementById("preciseTime").setTextContent(CommonTool.timestampSpilt(targetScore.getCreate_at())[1]);
            if (targetScore.getPp() != null)
            {
                doc.getElementById("totalPP").setTextContent(CommonTool.toString(Math.round(targetScore.getPp())).concat(" PP"));
                doc.getElementById("totalPPShadow").setTextContent(CommonTool.toString((int) Math.round(targetScore.getPp())).concat(" PP"));
            }
            else
            {
                doc.getElementById("totalPP").setTextContent("- PP");
                doc.getElementById("totalPPShadow").setTextContent("- PP");
            }

            doc.getElementById("accuracy").setTextContent(CommonTool.toString(targetScore.getAccuracy() * 100).concat("%"));
            doc.getElementById("accuracy-Shadow").setTextContent(CommonTool.toString(targetScore.getAccuracy() * 100).concat("%"));
            if (targetScore.getBeatmap().getMax_combo() != null)
            {
                doc.getElementById("comboStatus").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x/")
                        .concat(CommonTool.toString(targetScore.getBeatmap().getMax_combo())
                                .concat("x")).concat(" (").
                        concat(CommonTool.toString(((double) targetScore.getMaxCombo() / (double) targetScore.getBeatmap().getMax_combo()) * 100.0).concat("%)")));
                doc.getElementById("comboStatus-Shadow").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x/")
                        .concat(CommonTool.toString(targetScore.getBeatmap().getMax_combo())
                                .concat("x")).concat(" (").
                        concat(CommonTool.toString(((double) targetScore.getMaxCombo() / (double) targetScore.getBeatmap().getMax_combo()) * 100.0).concat("%)")));
            }
            else
            {
                doc.getElementById("comboStatus").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x"));
                doc.getElementById("comboStatus-Shadow").setTextContent(CommonTool.toString(targetScore.getMaxCombo()).concat("x"));
            }
            OsuMode mode= OsuMode.getMode(targetScore.getMode());

            if (targetScore.getPpDetailsLocal() != null)
            {
                String aimPPTotal = String.valueOf(Math.round(targetScore.getPpDetailsLocal().getAimPPMax()));
                String spdPPTotal = String.valueOf(Math.round(targetScore.getPpDetailsLocal().getSpdPPMax()));
                String accPPTotal = String.valueOf(Math.round(targetScore.getPpDetailsLocal().getAccPPMax()));
                aimPPTotal = aimPPTotal.length() > 4 ? "9999" : aimPPTotal;
                spdPPTotal = spdPPTotal.length() > 4 ? "9999" : spdPPTotal;
                accPPTotal = accPPTotal.length() > 4 ? "9999" : accPPTotal;
                for (int i = 0; i < aimPPTotal.length(); i++)
                {
                    doc.getElementById("aimPPTotal-" + (4 - i)).setTextContent(aimPPTotal.substring(aimPPTotal.length() - i - 1, aimPPTotal.length() - i));
                    doc.getElementById("aimPPTotal-" + (4 - i) + "-Shadow").setTextContent(aimPPTotal.substring(aimPPTotal.length() - i - 1, aimPPTotal.length() - i));
                    doc.getElementById("aimPPTotal-" + (4 - i)).setAttribute("opacity", "1");
                    doc.getElementById("aimPPTotal-" + (4 - i) + "-Shadow").setAttribute("opacity", "0.4");
                }
                for (int i = 0; i < spdPPTotal.length(); i++)
                {
                    doc.getElementById("spdPPTotal-" + (4 - i)).setTextContent(spdPPTotal.substring(spdPPTotal.length() - i - 1, spdPPTotal.length() - i));
                    doc.getElementById("spdPPTotal-" + (4 - i) + "-Shadow").setTextContent(spdPPTotal.substring(spdPPTotal.length() - i - 1, spdPPTotal.length() - i));
                    doc.getElementById("spdPPTotal-" + (4 - i)).setAttribute("opacity", "1");
                    doc.getElementById("spdPPTotal-" + (4 - i) + "-Shadow").setAttribute("opacity", "0.4");
                }
                for (int i = 0; i < accPPTotal.length(); i++)
                {
                    doc.getElementById("accPPTotal-" + (4 - i)).setTextContent(accPPTotal.substring(accPPTotal.length() - i - 1, accPPTotal.length() - i));
                    doc.getElementById("accPPTotal-" + (4 - i) + "-Shadow").setTextContent(accPPTotal.substring(accPPTotal.length() - i - 1, accPPTotal.length() - i));
                    doc.getElementById("accPPTotal-" + (4 - i)).setAttribute("opacity", "1");
                    doc.getElementById("accPPTotal-" + (4 - i) + "-Shadow").setAttribute("opacity", "0.4");
                }

                if(mode== OsuMode.Osu)
                {
                    String aimPPGet = String.valueOf(Math.round(targetScore.getPpDetailsLocal().getAimPP()));
                    String accPPGet = String.valueOf(Math.round(targetScore.getPpDetailsLocal().getAccPP()));
                    String spdPPGet = String.valueOf(Math.round(targetScore.getPpDetailsLocal().getSpdPP()));
                    aimPPGet = aimPPGet.length() > 4 ? "9999" : aimPPGet;
                    spdPPGet = spdPPGet.length() > 4 ? "9999" : spdPPGet;
                    accPPGet = accPPGet.length() > 4 ? "9999" : accPPGet;
                    for (int i = 0; i < aimPPGet.length(); i++)
                    {
                        doc.getElementById("aimPPGet-" + (4 - i)).setTextContent(aimPPGet.substring(aimPPGet.length() - i - 1, aimPPGet.length() - i));
                        doc.getElementById("aimPPGet-" + (4 - i) + "-Shadow").setTextContent(aimPPGet.substring(aimPPGet.length() - i - 1, aimPPGet.length() - i));
                        doc.getElementById("aimPPGet-" + (4 - i)).setAttribute("opacity", "1");
                        doc.getElementById("aimPPGet-" + (4 - i) + "-Shadow").setAttribute("opacity", "0.4");
                    }
                    for (int i = 0; i < spdPPGet.length(); i++)
                    {
                        doc.getElementById("spdPPGet-" + (4 - i)).setTextContent(spdPPGet.substring(spdPPGet.length() - i - 1, spdPPGet.length() - i));
                        doc.getElementById("spdPPGet-" + (4 - i) + "-Shadow").setTextContent(spdPPGet.substring(spdPPGet.length() - i - 1, spdPPGet.length() - i));
                        doc.getElementById("spdPPGet-" + (4 - i)).setAttribute("opacity", "1");
                        doc.getElementById("spdPPGet-" + (4 - i) + "-Shadow").setAttribute("opacity", "0.4");
                    }
                    for (int i = 0; i < accPPGet.length(); i++)
                    {
                        doc.getElementById("accPPGet-" + (4 - i)).setTextContent(accPPGet.substring(accPPGet.length() - i - 1, accPPGet.length() - i));
                        doc.getElementById("accPPGet-" + (4 - i) + "-Shadow").setTextContent(accPPGet.substring(accPPGet.length() - i - 1, accPPGet.length() - i));
                        doc.getElementById("accPPGet-" + (4 - i)).setAttribute("opacity", "1");
                        doc.getElementById("accPPGet-" + (4 - i) + "-Shadow").setAttribute("opacity", "0.4");
                    }
                }

                doc.getElementById("iffc").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getIfFc())).concat(" PP"));
                doc.getElementById("iffc-Shadow").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getIfFc())).concat(" PP"));

                doc.getElementById("95%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(95))).concat(" PP"));
                doc.getElementById("97%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(97))).concat(" PP"));
                doc.getElementById("98%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(98))).concat(" PP"));
                doc.getElementById("99%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(99))).concat(" PP"));
                doc.getElementById("100%pp").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(100))).concat(" PP"));
                doc.getElementById("95%pp-Shadow").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(95))).concat(" PP"));
                doc.getElementById("97%pp-Shadow").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(97))).concat(" PP"));
                doc.getElementById("98%pp-Shadow").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(98))).concat(" PP"));
                doc.getElementById("99%pp-Shadow").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(99))).concat(" PP"));
                doc.getElementById("100%pp-Shadow").setTextContent(CommonTool.toString(Math.round(targetScore.getPpDetailsLocal().getAccPPList().get(100))).concat(" PP"));
            }
            doc.getElementById("score").setTextContent(CommonTool.transformNumber(String.valueOf(targetScore.getScore())));
            doc.getElementById("score-Shadow").setTextContent(CommonTool.transformNumber(String.valueOf(targetScore.getScore())));
            if (targetScore.getBeatmap().getVersion().length() < 20)
            {
                doc.getElementById("version").setTextContent(targetScore.getBeatmap().getVersion());
            }
            else
            {
                doc.getElementById("version").setTextContent(targetScore.getBeatmap().getVersion().substring(0, 19).concat("..."));
            }

            switch (mode)
            {
                case Osu:
                {
                    log.info("Score Type: Osu");
                    doc.getElementById("osu").setAttribute("fill", hue>360?"hsl(160, 100%, 50%)":CommonTool.hsvToHex(hue,0.4F,1F));
                    doc.getElementById("label-osu").setAttribute("opacity","1");
                    doc.getElementById("osuStatistics").setAttribute("opacity","1");
                    doc.getElementById("100Count-o").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
                    doc.getElementById("300Count-o").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("50Count-o").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
                    doc.getElementById("missCount-o").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));
                    doc.getElementById("100Count-Shadow-o").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
                    doc.getElementById("300Count-Shadow-o").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("50Count-Shadow-o").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
                    doc.getElementById("missCount-Shadow-o").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));

                    doc.getElementById("attribute-osu").setAttribute("opacity","1");
                    doc.getElementById("AR-osu").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getAr()));
                    doc.getElementById("OD-osu").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getOd()));
                    doc.getElementById("HP-osu").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getHp()));
                    doc.getElementById("CS-osu").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getCs()));
                    break;
                }
                case Taiko:
                {
                    log.info("Score Type: Taiko");
                    doc.getElementById("taiko").setAttribute("fill", hue>360?"hsl(160, 100%, 50%)":CommonTool.hsvToHex(hue,0.4F,1F));
                    doc.getElementById("label-taiko").setAttribute("opacity","1");
                    doc.getElementById("taikoStatistics").setAttribute("opacity","1");
                    doc.getElementById("150Count-t").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
                    doc.getElementById("300Count-t").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("missCount-t").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));
                    doc.getElementById("150Count-Shadow-t").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
                    doc.getElementById("300Count-Shadow-t").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("missCount-Shadow-t").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));

                    doc.getElementById("attribute-taiko").setAttribute("opacity","1");
                    doc.getElementById("OD-taiko").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getOd()));
                    doc.getElementById("HP-taiko").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getHp()));
                    break;
                }
                case Catch:
                {
                    log.info("Score Type: CTB");
                    doc.getElementById("ctb").setAttribute("fill", hue>360?"hsl(160, 100%, 50%)":CommonTool.hsvToHex(hue,0.4F,1F));
                    doc.getElementById("label-ctb").setAttribute("opacity","1");
                    doc.getElementById("fruitsStatistics").setAttribute("opacity","1");
                    doc.getElementById("300Count-f").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("100Count-f").setTextContent(String.valueOf(targetScore.getStatistics().getLarge_tick_hit()));
                    doc.getElementById("50Count-f").setTextContent(String.valueOf(targetScore.getStatistics().getSmall_tick_hit()));
                    doc.getElementById("missCount-f").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));
                    doc.getElementById("300Count-Shadow-f").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("100Count-Shadow-f").setTextContent(String.valueOf(targetScore.getStatistics().getLarge_tick_hit()));
                    doc.getElementById("50Count-Shadow-f").setTextContent(String.valueOf(targetScore.getStatistics().getSmall_tick_hit()));
                    doc.getElementById("missCount-Shadow-f").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));

                    doc.getElementById("attribute-catch").setAttribute("opacity","1");
                    doc.getElementById("AR-catch").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getAr()));
                    doc.getElementById("OD-catch").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getOd()));
                    doc.getElementById("HP-catch").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getHp()));
                    doc.getElementById("CS-catch").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getCs()));
                    break;
                }
                case Mania:
                {
                    log.info("Score Type: Mania");
                    doc.getElementById("mania").setAttribute("fill", hue>360?"hsl(160, 100%, 50%)":CommonTool.hsvToHex(hue,0.4F,1F));
                    doc.getElementById("label-mania").setAttribute("opacity","1");
                    doc.getElementById("maniaStatistics").setAttribute("opacity","1");
                    doc.getElementById("maxCount-m").setTextContent(String.valueOf(targetScore.getStatistics().getPerfect()));
                    doc.getElementById("300Count-m").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("200Count-m").setTextContent(String.valueOf(targetScore.getStatistics().getGood()));
                    doc.getElementById("100Count-m").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
                    doc.getElementById("50Count-m").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
                    doc.getElementById("missCount-m").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));
                    doc.getElementById("maxCount-Shadow-m").setTextContent(String.valueOf(targetScore.getStatistics().getPerfect()));
                    doc.getElementById("300Count-Shadow-m").setTextContent(String.valueOf(targetScore.getStatistics().getGreat()));
                    doc.getElementById("200Count-Shadow-m").setTextContent(String.valueOf(targetScore.getStatistics().getGood()));
                    doc.getElementById("100Count-Shadow-m").setTextContent(String.valueOf(targetScore.getStatistics().getOk()));
                    doc.getElementById("50Count-Shadow-m").setTextContent(String.valueOf(targetScore.getStatistics().getMeh()));
                    doc.getElementById("missCount-Shadow-m").setTextContent(String.valueOf(targetScore.getStatistics().getMiss()));

                    doc.getElementById("attribute-mania").setAttribute("opacity","1");
                    doc.getElementById("Key-mania").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getCs()));
                    doc.getElementById("OD-mania").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getOd()));
                    doc.getElementById("HP-mania").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getHp()));
                    break;
                }
            }
            if(hue<361) setupoCustomColorForDarkmodeScore(doc,hue);

            doc.getElementById("bpm").setTextContent(CommonTool.toString(targetScore.getBeatmap().getAttributes().getBpm()));
            doc.getElementById("length").setTextContent(CommonTool.formatHitLength(targetScore.getBeatmap().getAttributes().getLength()));

            if (targetScore.getBeatmap().getTitle().length() < 20)
            {
                doc.getElementById("songTitle").setTextContent(targetScore.getBeatmap().getTitle());
            }
            else
            {
                doc.getElementById("songTitle").setTextContent(targetScore.getBeatmap().getTitle().substring(0, 19).concat("..."));
            }
            doc.getElementById(targetScore.getBeatmap().getStatus()).setAttribute("opacity", "1");

            Element grade = doc.getElementById("grade");
            doc.getElementById("grade-Shadow").setTextContent(targetScore.getRank());
            grade.setTextContent(targetScore.getRank());
            grade.setAttribute("fill", RankColor.fromString(targetScore.getRank()).getDarkRankColorHEX());

            doc.getElementById("starRatingBG").setAttribute("fill", "#".concat(CommonTool.calcDiffColor(targetScore.getBeatmap().getDifficult_rating())));

            if (targetScore.getModJSON() != null && targetScore.getModJSON().size() > 0) {
                if (!targetScore.getIsLazer()) targetScore.setModJSON(targetScore.getModJSON().stream().filter(mod -> !mod.getAcronym().equals("CL")).toList());
                for(int i=0;i<targetScore.getModJSON().size();i++) {
                    ModColor color=ModColor.fromString(targetScore.getModJSON().get(i).getAcronym());
                    wireModIconForDarkScore(doc,
                            i,
                            targetScore.getModJSON().get(i),
                            color.getDetailedPrimaryColor().toString(),
                            color.getDetailedSecondaryColor().toString(),
                            color.getDetailedSideColor().toString()
                    );
                }
            }
            log.info("Batik SVG util cost (dark mode): " + (System.currentTimeMillis() - startingTime) + "ms");
            return doc;
        } catch (Exception e)
        {
            log.error(e.getMessage());
            throw new LazybotRuntimeException("暗黑模式Score panel生成失败");
        }

    }
    private static void setupoCustomColorForDarkmodeScore(Document doc, Integer hue){
        doc.getElementById("label-left-background").setAttribute("fill",CommonTool.hslFormat(hue,12,22));
        doc.getElementById("label-left-left-background").setAttribute("fill",CommonTool.hslFormat(hue,12,13));
        doc.getElementById("label-artist").setAttribute("fill",CommonTool.hslFormat(hue,37,68));
        doc.getElementById("label-mapper").setAttribute("fill",CommonTool.hslFormat(hue,37,68));
        doc.getElementById("label-mapInfo").setAttribute("fill",CommonTool.hslFormat(hue,37,68));
        doc.getElementById("label-diff").setAttribute("fill",CommonTool.hslFormat(hue,37,68));
        doc.getElementById("label-title").setAttribute("fill",CommonTool.hslFormat(hue,37,68));
        doc.getElementById("label-mask-1").setAttribute("fill",CommonTool.hslFormat(hue,11,18));
        doc.getElementById("label-mask-2").setAttribute("fill",CommonTool.hslFormat(hue,11,18));
        doc.getElementById("label-mask-3").setAttribute("fill",CommonTool.hslFormat(hue,11,18));
        doc.getElementById("label-header-bg").setAttribute("fill",CommonTool.hslFormat(hue,7,10));
    }
    private static void wireModIconForDarkScore(Document doc, int index, Mod mod, String color, String color2, String color3)
    {
        Element svgRoot = doc.getDocumentElement();
        Node sectionFullNode = doc.createElementNS(namespaceSVG, "g");
        Element sectionFull = (Element) sectionFullNode;
        sectionFull.setAttribute("transform", "translate(" + index*100 +",0)");

        Node modBGNode = doc.createElementNS(namespaceSVG, "rect");
        Element modBG  = (Element) modBGNode ;
        modBG .setAttribute("rx", "10");
        modBG.setAttribute("ry", "10");
        modBG.setAttribute("x", "1470");
        modBG.setAttribute("y", "530");
        modBG.setAttribute("width", "120");
        modBG.setAttribute("height", "70");
        modBG.setAttribute("fill", color);
        modBG.setAttribute("transform", "skewX(-20)");

        Node modBGNode2 = doc.createElementNS(namespaceSVG, "rect");
        Element modBG2  = (Element) modBGNode2 ;
        modBG2.setAttribute("rx", "10");
        modBG2.setAttribute("ry", "10");
        modBG2.setAttribute("x", "1520");
        modBG2.setAttribute("y", "550");
        modBG2.setAttribute("width", "70");
        modBG2.setAttribute("height", "50");
        modBG2.setAttribute("fill", color2);
        modBG2.setAttribute("transform", "skewX(-20)");

        Node modBGNode3 = doc.createElementNS(namespaceSVG, "rect");
        Element modBG3  = (Element) modBGNode3 ;
        modBG3.setAttribute("rx", "10");
        modBG3.setAttribute("ry", "10");
        modBG3.setAttribute("x", "1470");
        modBG3.setAttribute("y", "530");
        modBG3.setAttribute("width", "50");
        modBG3.setAttribute("height", "30");
        modBG3.setAttribute("fill", color3);
        modBG3.setAttribute("transform", "skewX(-20)");

        Node modNameNode = doc.createElementNS(namespaceSVG, "text");
        Element modName  = (Element) modNameNode ;
        modName.setAttribute("class", "cls-124");
        modName.setAttribute("x", "1495");
        modName.setAttribute("y", "580");
        modName.setAttribute("transform", "skewX(-10) rotate(-10,1480,1160)");
        modName.setTextContent(mod.getAcronym());
        sectionFull.appendChild(modBGNode);
        sectionFull.appendChild(modBGNode2);
        sectionFull.appendChild(modBGNode3);
        sectionFull.appendChild(modNameNode);

        if(mod.getAcronym().equals("DT")||mod.getAcronym().equals("NC")||mod.getAcronym().equals("HT")||mod.getAcronym().equals("DC")) {
            Node modClockRateNode = doc.createElementNS(namespaceSVG, "text");
            Element modeClockRate = (Element) modClockRateNode;
            modeClockRate.setAttribute("class", "cls-125");
            modeClockRate.setAttribute("x", "1495");
            modeClockRate.setAttribute("y", "580");
            modeClockRate.setAttribute("transform", "skewX(-10) rotate(-10,1550,1000)");
            if(mod.getSettings().getSpeed_change()!=null) {
                modeClockRate.setTextContent(CommonTool.toString(mod.getSettings().getSpeed_change()).concat("x"));
            }
            else {
                if(mod.getAcronym().equals("DT")||mod.getAcronym().equals("NC")) modeClockRate.setTextContent("1.50x");
                else modeClockRate.setTextContent("0.75x");

                modeClockRate.setAttribute("opacity", "0.2");
            }
            sectionFull.appendChild(modeClockRate);
        }
        svgRoot.appendChild(sectionFull);
    }

    public static Document renderScoreToImage(ScoreVO targetScore, int version, int[] primaryColor)
    {

        Document doc;
        if (version==0)
            doc = mapScoreToScorePanelDark(targetScore,primaryColor);
        else if (version==1)
            doc = mapScoreToScorePanelWhite(targetScore);
        else if (version==2)
            doc = mapScoreToScorePanelMaterial(targetScore,primaryColor);
        else throw new LazybotRuntimeException("[Lazybot] 不支持的面板版本: " + version);
        return doc;
    }
}
