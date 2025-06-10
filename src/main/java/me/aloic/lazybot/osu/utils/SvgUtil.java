package me.aloic.lazybot.osu.utils;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.osu.enums.*;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.util.CommonTool;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.osu.extended.rosu.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Svg操作类
 * 说实话不知道怎么简化，而且看着是真头疼，
 * 很可惜只有Apache Batik可以修改/转化成png，
 * JFreeSvg有调查，的确是比Batik更高效，但是他不包含修改功能只有生成svg，
 * 所以只能用这个了，
 * 但是实际看看，根据score来修改svg实际上也只需要100ms左右，
 * 大部分时间消耗在带宽和，转码上面
 * **/


@SuppressWarnings("all")
public class SvgUtil
{
    private static final PNGTranscoder transcoder = new PNGTranscoder();
    private static  Transformer transformer;
    private static final Logger logger = LoggerFactory.getLogger(SvgUtil.class);
    private static final String namespaceSVG = "http://www.w3.org/2000/svg";
    private static final String xlinkns = "http://www.w3.org/1999/xlink";

    static{
         transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, Boolean.TRUE);
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e)
        {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("Batik转换器初始化失败");
        }
    }

    public static OutputStream scoreToImageApache(ScoreVO targetScore) throws TranscoderException, IOException
    {
        long startingTime = System.currentTimeMillis();
        TranscoderInput input = new TranscoderInput(getScorePanelWhiteModeDoc(targetScore));
        OutputStream o=svgToPng(input);
        logger.info("Render cost(white mode):" + (System.currentTimeMillis() - startingTime) + "ms");
        return o;
    }

    public static String scoreToImageURLApache(ScoreVO targetScore) throws TranscoderException, IOException
    {
        long startingTime = System.currentTimeMillis();
        Document doc = getScorePanelWhiteModeDoc(targetScore);
        TranscoderInput input = new TranscoderInput(doc);
        String outputName = "score-".concat(targetScore.getUser_name()).concat(".png");
        svgToPng(input, new File(outputName));
        logger.info("Total Build time:" + (System.currentTimeMillis() - startingTime) + "ms");
        return outputName;
    }

    public static OutputStream scoreToImageDarkApache(ScoreVO targetScore, int[] primaryColor) throws TranscoderException, IOException
    {
        Document doc = getScorePanelDarkModeDoc(targetScore,primaryColor);
        long startingTime = System.currentTimeMillis();
        TranscoderInput input = new TranscoderInput(doc);
        OutputStream o =svgToPng(input);
        logger.info("Render cost(dark mode):" + (System.currentTimeMillis() - startingTime) + "ms");
        return o;
    }

    public static String scoreToImageDarkURLApache(ScoreVO targetScore, int[] primaryColor) throws TranscoderException, IOException
    {
        Document doc = getScorePanelDarkModeDoc(targetScore,primaryColor);
        TranscoderInput input = new TranscoderInput(doc);
        String outputName = "score-dark-".concat(targetScore.getUser_name()).concat(".png");
        svgToPng(input, new File(outputName));
        return outputName;
    }

    private static void wireModIcon(Document doc, int index, String modName, String modColor, String subModColor1, String subModColor2)
    {
        doc.getElementById("mod" + (index + 1)).setAttribute("opacity", "1");
        doc.getElementById("modBG" + (index + 1) + "-0").setAttribute("fill", "#".concat(modColor));
        doc.getElementById("modBG" + (index + 1) + "-1").setAttribute("fill", "#".concat(subModColor1));
        doc.getElementById("modBG" + (index + 1) + "-2").setAttribute("fill", "#".concat(subModColor2));
        doc.getElementById("modText" + (index + 1)).setTextContent(modName);
    }

    private static Element wireModIconForList(Document document, int index, String modName, String modColor, int type)
    {
        Node modFullNode = document.createElementNS(namespaceSVG, "g");
        Element modFull = (Element) modFullNode;
        Node modBGNode = document.createElementNS(namespaceSVG, "rect");
        Element modBG = (Element) modBGNode;

        Node modTextNode = document.createElementNS(namespaceSVG, "text");
        Element modText = (Element) modTextNode;
        modText.setAttribute("class", "cls-124");
        modText.setAttribute("fill", "#2a2933");
        modText.setAttribute("transform", "skewX(-5)");
        if (type == 0)
        {
            modBG.setAttribute("x", "700");
            modBG.setAttribute("y", "475");
            modBG.setAttribute("rx", "10");
            modBG.setAttribute("ry", "10");
            modBG.setAttribute("fill", modColor);
            modBG.setAttribute("width", "60");
            modBG.setAttribute("height", "50");
            modBG.setAttribute("transform", "skewX(-5)");
            modText.setAttribute("x", "708");
            modText.setAttribute("y", "510");
        }
        else if (type == 1)
        {
            modBG.setAttribute("x", "622");
            modBG.setAttribute("y", "117");
            modBG.setAttribute("rx", "7");
            modBG.setAttribute("ry", "7");
            modBG.setAttribute("fill", modColor);
            modBG.setAttribute("width", "45");
            modBG.setAttribute("height", "40");
            modBG.setAttribute("transform", "skewX(-5)");
            modText.setAttribute("x", "626.5");
            modText.setAttribute("y", "146.5");
        }
        else {
            throw new LazybotRuntimeException("创建List样式的Mod图标时出错: 类型越界=" +type);
        }
        modText.setTextContent(modName);
        modFull.appendChild(modBGNode);
        modFull.appendChild(modTextNode);
        modFull.setAttribute("transform", "translate(".concat(String.valueOf(-30 * (index))).concat(",0 )"));
        return modFull;
    }

    public static void createListSubSection(Document document, ScoreVO scoreVO, int index)
    {
        Element svgRoot = document.getDocumentElement();
        Node sectionFullNode = document.createElementNS(namespaceSVG, "g");
        Element sectionFull = (Element) sectionFullNode;
        Node listSubSectionNode = document.createElementNS(namespaceSVG, "rect");
        Element listSubSection = (Element) listSubSectionNode;
        listSubSection.setAttribute("class", "cls-4");
        listSubSection.setAttribute("x", "40");
        listSubSection.setAttribute("y", "455");
        listSubSection.setAttribute("width", "1000");
        listSubSection.setAttribute("height", "90");
        listSubSection.setAttribute("rx", "10");
        listSubSection.setAttribute("ry", "10");

        Node songTitleNode = document.createElementNS(namespaceSVG, "text");
        Element songTitle = (Element) songTitleNode;
        String title = scoreVO.getBeatmap().getTitle();
        if (title.length() > 20)
        {
            title = title.substring(0, 20) + "...";
        }
        songTitle.setAttribute("class", "cls-16");
        songTitle.setAttribute("transform", "translate(140 495)");
        songTitle.setTextContent(title);

        String diff = scoreVO.getBeatmap().getVersion();
        if (diff.length() > 17)
        {
            diff = diff.substring(0, 16) + "...";
        }
        Node difficultyNode = document.createElementNS(namespaceSVG, "text");
        Element difficulty = (Element) difficultyNode;
        difficulty.setAttribute("class", "cls-26");
        difficulty.setAttribute("transform", "translate(250 530)");
        difficulty.setTextContent("["+diff+"]");

        Node starBGNode = document.createElementNS(namespaceSVG, "rect");
        Element starBG = (Element) starBGNode;
        starBG.setAttribute("fill", "#".concat(CommonTool.calcDiffColor(scoreVO.getBeatmap().getDifficult_rating())));
        starBG.setAttribute("x", "140");
        starBG.setAttribute("y", "511");
        starBG.setAttribute("width", "85");
        starBG.setAttribute("height", "25");
        starBG.setAttribute("rx", "13");
        starBG.setAttribute("ry", "13");


        String accColor = "#fed867";
        if (scoreVO.getBeatmap().getDifficult_rating() < 7.0)
        {
            if (scoreVO.getBeatmap().getDifficult_rating() % 1.0 < 0.5)
            {
                accColor = "#1c1719";
            }
        }
        Node starNode = document.createElementNS(namespaceSVG, "text");
        Element star = (Element) starNode;
        star.setAttribute("class", "cls-125");
        star.setAttribute("transform", "translate(184 531)");
        star.setAttribute("text-anchor", "middle");
        star.setAttribute("fill", accColor);
        star.setTextContent(String.valueOf(scoreVO.getBeatmap().getDifficult_rating()).concat("*"));


        Node accNode = document.createElementNS(namespaceSVG, "text");
        Element acc = (Element) accNode;
        acc.setAttribute("class", "cls-21");
        acc.setAttribute("transform", "translate(750 495)");
        acc.setTextContent(CommonTool.toString(scoreVO.getAccuracy() * 100).concat("%"));

        Node ppValueNode = document.createElementNS(namespaceSVG, "text");
        Element ppValue = (Element) ppValueNode;
        ppValue.setAttribute("class", "cls-23");
        ppValue.setAttribute("transform", "translate(910 520)");
        ppValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())));

        Node ppLabelNode = document.createElementNS(namespaceSVG, "text");
        Element ppLabel = (Element) ppLabelNode;
        ppLabel.setAttribute("class", "cls-25");
        ppLabel.setAttribute("transform", "translate(990 478)");
        ppLabel.setTextContent("PP");

        Node comboNode = document.createElementNS(namespaceSVG, "text");
        Element combo = (Element) comboNode;
        combo.setAttribute("class", "cls-15");
        combo.setAttribute("transform", "translate(750 525)");
        combo.setTextContent(String.valueOf(scoreVO.getMaxCombo()).concat("x"));

        Node gradeNode = document.createElementNS(namespaceSVG, "image");
        Element grade = (Element) gradeNode;
        grade.setAttributeNS(xlinkns, "xlink:href", "assets/osuResources/GradeSmall-".concat(scoreVO.getRank()).concat(".svg"));
        grade.setAttribute("x", "60");
        grade.setAttribute("y", "485");
        grade.setAttribute("width", "60");
        grade.setAttribute("height", "30");
        grade.setAttribute("preserveAspectRatio", "xMidYMid slice");

        sectionFull.setAttribute("transform", "translate(0 ".concat(String.valueOf(120 * (index - 1) - 65)).concat(")"));
        sectionFull.appendChild(listSubSection);
        sectionFull.appendChild(songTitle);
        sectionFull.appendChild(difficulty);
        sectionFull.appendChild(starBG);
        sectionFull.appendChild(star);
        sectionFull.appendChild(acc);
        sectionFull.appendChild(ppValue);
        sectionFull.appendChild(ppLabel);
        sectionFull.appendChild(combo);
        sectionFull.appendChild(grade);

        if (scoreVO.getMods() != null)
        {
            scoreVO.setMods(Arrays.stream(scoreVO.getMods())
                    .filter(score -> !score.equals("CL"))
                    .toArray(String[]::new));
            for(int i=0;i<scoreVO.getMods().length;i++) {
                sectionFull.appendChild(wireModIconForList(document,
                        i,
                        scoreVO.getMods()[i],
                        ModColor.fromString(scoreVO.getMods()[i]).getDetailedSideColor().toString(),
                        0));
            }
        }
        svgRoot.appendChild(sectionFull);
    }

    public static void createCompareListSubSection(Document document, ScoreVO scoreVO, int index, int type)
    {
        Element svgRoot = document.getDocumentElement();
        String xlinkns = "http://www.w3.org/1999/xlink";
        String nameSpace = "http://www.w3.org/2000/svg";
        Node sectionFullNode = document.createElementNS(nameSpace, "g");
        Element sectionFull = (Element) sectionFullNode;
        Node listSubSectionNode = document.createElementNS(nameSpace, "rect");
        Element listSubSection = (Element) listSubSectionNode;
        listSubSection.setAttribute("class", "cls-1");
        listSubSection.setAttribute("x", "20");
        listSubSection.setAttribute("y", "100");
        listSubSection.setAttribute("width", "900");
        listSubSection.setAttribute("height", "70");
        listSubSection.setAttribute("rx", "10");
        listSubSection.setAttribute("ry", "10");


        Node songTitleNode = document.createElementNS(nameSpace, "text");
        Element songTitle = (Element) songTitleNode;
        String title = scoreVO.getBeatmap().getTitle();
        if (title.length() > 20)
        {
            title = title.substring(0, 20) + "...";
        }
        songTitle.setAttribute("class", "cls-116");
        songTitle.setAttribute("transform", "translate(120 130)");
        songTitle.setTextContent(title);

        String diff = scoreVO.getBeatmap().getVersion();
        if (diff.length() > 16)
        {
            diff = diff.substring(0, 16) + "...";
        }
        Node difficultyNode = document.createElementNS(nameSpace, "text");
        Element difficulty = (Element) difficultyNode;
        difficulty.setAttribute("class", "cls-126");
        difficulty.setAttribute("transform", "translate(216 160)");
        difficulty.setTextContent(diff);

        Node starBGNode = document.createElementNS(nameSpace, "rect");
        Element starBG = (Element) starBGNode;
        starBG.setAttribute("fill", "#".concat(CommonTool.calcDiffColor(scoreVO.getBeatmap().getDifficult_rating())));
        starBG.setAttribute("x", "120");
        starBG.setAttribute("y", "146");
        starBG.setAttribute("width", "68");
        starBG.setAttribute("height", "20");
        starBG.setAttribute("rx", "10");
        starBG.setAttribute("ry", "10");


        String accColor = "#fed867";
        if (scoreVO.getBeatmap().getDifficult_rating() < 7.0)
        {
            if (scoreVO.getBeatmap().getDifficult_rating() % 1.0 < 0.5)
            {
                accColor = "#1c1719";
            }
        }
        Node starNode = document.createElementNS(nameSpace, "text");
        Element star = (Element) starNode;
        star.setAttribute("class", "cls-155");
        star.setAttribute("transform", "translate(160 162.5)");
        star.setAttribute("text-anchor", "middle");
        star.setAttribute("fill", accColor);
        star.setTextContent(CommonTool.toString(scoreVO.getBeatmap().getDifficult_rating()));

        Node starPolyNode = document.createElementNS(nameSpace, "polygon");
        Element starPoly = (Element) starPolyNode;
        starPoly.setAttribute("transform", "translate(45 -3) scale(0.43)");
        starPoly.setAttribute("points", "200 355.86 204.16 364.28 213.45 365.63 206.72 372.19 208.31 381.44 200 377.07 191.69 381.44 193.28 372.19 186.55 365.63 195.84 364.28 200 355.86");
        starPoly.setAttribute("fill", accColor);


        Node accNode = document.createElementNS(nameSpace, "text");
        Element acc = (Element) accNode;
        acc.setAttribute("class", "cls-121");
        acc.setAttribute("transform", "translate(690 130)");
        acc.setTextContent(CommonTool.toString(scoreVO.getAccuracy() * 100).concat("%"));

        Node ppValueNode = document.createElementNS(nameSpace, "text");
        Element ppValue = (Element) ppValueNode;
        ppValue.setAttribute("class", "cls-123");
        ppValue.setAttribute("transform", "translate(810 152)");
        ppValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())));

        Node ppLabelNode = document.createElementNS(nameSpace, "text");
        Element ppLabel = (Element) ppLabelNode;
        ppLabel.setAttribute("class", "cls-125");
        ppLabel.setAttribute("transform", "translate(890 120)");
        ppLabel.setTextContent("PP");

        Node comboNode = document.createElementNS(nameSpace, "text");
        Element combo = (Element) comboNode;
        combo.setAttribute("class", "cls-115");
        combo.setAttribute("transform", "translate(690 160)");
        combo.setTextContent(String.valueOf(scoreVO.getMaxCombo()).concat("x"));

        Node gradeNode = document.createElementNS(nameSpace, "image");
        Element grade = (Element) gradeNode;
        grade.setAttributeNS(xlinkns, "xlink:href", "assets/osuResources/GradeSmall-".concat(scoreVO.getRank()).concat(".svg"));
        grade.setAttribute("x", "40");
        grade.setAttribute("y", "120");
        grade.setAttribute("width", "60");
        grade.setAttribute("height", "30");
        grade.setAttribute("preserveAspectRatio", "xMidYMid slice");

        sectionFull.appendChild(listSubSection);
        sectionFull.appendChild(songTitle);
        sectionFull.appendChild(difficulty);
        sectionFull.appendChild(starBG);
        sectionFull.appendChild(star);
        sectionFull.appendChild(starPoly);
        sectionFull.appendChild(acc);
        sectionFull.appendChild(ppValue);
        sectionFull.appendChild(ppLabel);
        sectionFull.appendChild(combo);
        sectionFull.appendChild(grade);

        if (scoreVO.getMods() != null) {
            scoreVO.setMods(Arrays.stream(scoreVO.getMods())
                .filter(score -> !score.equals("CL"))
                .toArray(String[]::new));
            for (int i = 0; i < scoreVO.getMods().length; i++) {
                sectionFull.appendChild(
                        wireModIconForList(document,
                                i,
                                scoreVO.getMods()[i],
                                ModColor.fromString(scoreVO.getMods()[i]).getDetailedSideColor().toString(),
                                1));
            }
        }
        if (type == 0) {
            sectionFull.setAttribute("transform", "translate(0 ".concat(String.valueOf(index * 85)).concat(")"));
        }
        else
        {
            sectionFull.setAttribute("transform", "translate(870 ".concat(String.valueOf(index * 85)).concat(")"));
        }
        svgRoot.appendChild(sectionFull);
    }

    public static Document getScorePanelWhiteModeDoc(ScoreVO targetScore)
    {
        long startingTime = System.currentTimeMillis();
        try
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/scorePanelSimplifiedV4.svg");
            URI inputUri = filePath.toFile().toURI();
            Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
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
            logger.info("Batik Util Cost (white Mode):" + (System.currentTimeMillis() - startingTime) + "ms");
            return doc;
        } catch (Exception e)
        {
           logger.error("Error while generating score panel: {}",e);
            throw new LazybotRuntimeException("亮色模式Score panel生成失败");
        }
    }
    public static Document getScorePanelMaterialDesign(ScoreVO targetScore,int[] primaryColor)
    {
        try{
            List<Double> hsl = CommonTool.rgbToHslDetailed(primaryColor);
            String color =String.format("hsl(%.0f, %.0f%%, %.0f%%)", hsl.get(0),  hsl.get(1) * 100,  (hsl.get(2) * 100)+20>94?94:(hsl.get(2) * 100)+20);

            Path filePath = ResourceMonitor.getResourcePath().resolve("static/ScorePanelMaterialDesign-Reranged.svg");
            URI inputUri = filePath.toFile().toURI();
            Document document = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
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
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("生成Material Score图像时出错");
        }
    }
    public static Document getScorePanelDarkModeDoc(ScoreVO targetScore,int[] primaryColor)
    {
        long startingTime = System.currentTimeMillis();

        try {
            int hue=CommonTool.rgbToHue(primaryColor);
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/scorePanelDarkmode_customize.svg");
            URI inputUri = filePath.toFile().toURI();
            Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
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
                    logger.info("Score Type: Osu");
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
                    logger.info("Score Type: Taiko");
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
                    logger.info("Score Type: CTB");
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
                    logger.info("Score Type: Mania");
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
            logger.info("Batik SVG util cost (dark mode): " + (System.currentTimeMillis() - startingTime) + "ms");
            return doc;
        } catch (Exception e)
        {
            logger.error(e.getMessage());
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

    public static Document createCompareBpList(PlayerInfoDTO currentPlayer, PlayerInfoDTO comparedPlayer,
                                               ScoreVO[] scoreVOArray, ScoreVO[] compareScoreVOArray) throws IOException
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/scoreListCompareFull.svg");
        URI inputUri = filePath.toFile().toURI();
        Document document = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
        Element svgRoot = document.getDocumentElement();
        int totalCount=scoreVOArray.length+compareScoreVOArray.length;
        ScoreVO[] allScores=new ScoreVO[totalCount];
        System.arraycopy(scoreVOArray, 0, allScores, 0, scoreVOArray.length);
        System.arraycopy(compareScoreVOArray, 0, allScores, scoreVOArray.length, compareScoreVOArray.length);
        Arrays.sort(allScores, Comparator.comparing(ScoreVO::getPp).reversed());
        int targetHeight=180+totalCount*85;
        svgRoot.setAttribute("height", String.valueOf(targetHeight));
        document.getElementById("totalBackground").setAttribute("height", String.valueOf(targetHeight));
        document.getElementById("playerName-0").setTextContent(currentPlayer.getUsername());
        document.getElementById("ppValue-0").setTextContent(String.valueOf(Math.round(currentPlayer.getStatistics().getPp())));
        document.getElementById("playerName-1").setTextContent(comparedPlayer.getUsername());
        document.getElementById("ppValue-1").setTextContent(String.valueOf(Math.round(comparedPlayer.getStatistics().getPp())));
        document.getElementById("avatar-0").setAttributeNS(xlinkns, "xlink:href", currentPlayer.getAvatar_url());
        document.getElementById("avatar-1").setAttributeNS(xlinkns, "xlink:href", comparedPlayer.getAvatar_url());
        document.getElementById("roughTime").setTextContent(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        document.getElementById(OsuMode.getMode(scoreVOArray[0].getMode()).getDescribe()).setAttribute("class", "cls-4");
        for(int i=0;i<totalCount;i++)
        {
            if(Objects.equals(allScores[i].getUser_name(), currentPlayer.getUsername()))
            {
                SvgUtil.createCompareListSubSection(document,allScores[i],i,0);
            }
            else
            {
                SvgUtil.createCompareListSubSection(document,allScores[i],i,1);
            }
        }
        return document;
    }
    public static Document createBpList(String playerName, ScoreVO[] scoreVOArray, Integer totalCount, Integer offset) throws IOException
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/scoreListDark.svg");
        URI inputUri = filePath.toFile().toURI();
        Document document = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
        Element svgRoot = document.getDocumentElement();
        int targetHeight=470+115*totalCount-65;
        svgRoot.setAttribute("height", String.valueOf(targetHeight));
        document.getElementById("background").setAttribute("height", String.valueOf(targetHeight));

        document.getElementById("playerName").setTextContent(playerName);
        document.getElementById("ppValue").setTextContent("-");

        document.getElementById("type").setTextContent("BP LIST");
        document.getElementById("title-1").setTextContent(scoreVOArray[0].getBeatmap().getTitle());
        document.getElementById("artist-1").setTextContent(scoreVOArray[0].getBeatmap().getArtist());
        document.getElementById("mapper-1").setTextContent(scoreVOArray[0].getBeatmap().getCreator());
        document.getElementById("acc-1").setTextContent(CommonTool.toString(scoreVOArray[0].getAccuracy()*100).concat("%"));
        document.getElementById("grade-1").setTextContent(scoreVOArray[0].getRank());
        document.getElementById("bid-1").setTextContent(String.valueOf(scoreVOArray[0].getBeatmap().getBid()));
        document.getElementById("star-1").setTextContent(String.valueOf(scoreVOArray[0].getBeatmap().getDifficult_rating()));
        document.getElementById("pp-1").setTextContent(CommonTool.toString(Math.round(scoreVOArray[0].getPp())));
        document.getElementById("section-main-BG");
        String offsetString=String.valueOf(offset);
        if(offset<10)
        {
            offsetString="0".concat(String.valueOf(offset));
        }
        document.getElementById("index-1").setTextContent("#".concat(offsetString));

        for(int i=1;i<totalCount;i++)
        {
            SvgUtil.createListSubSection(document,scoreVOArray[i],i);
        }
        return document;
    }
    public static Document createScoreListDetailed(List<ScoreSequence> scorelist, String primaryColor, String type,Integer offset) throws IOException
    {
        try
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/TopScoresList.svg");
            URI inputUri = filePath.toFile().toURI();
            Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
            Element svgRoot = doc.getDocumentElement();
            String totalHeight = String.valueOf(130 + 120 * scorelist.size());
            svgRoot.setAttribute("height", totalHeight);
            doc.getElementById("background").setAttribute("height", totalHeight);
            doc.getElementById("StaticCommandName").setTextContent(type);
            doc.getElementById("footer").setAttribute("transform", "translate(0," + 120 * (scorelist.size() - 1) + ")");
            doc.getElementById("requestTime").setTextContent(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            doc.getElementById(OsuMode.getMode(scorelist.get(0).getRulesetId()).getDescribe()).setAttribute("class", "cls-24");
            doc.getElementById("label-".concat(OsuMode.getMode(scorelist.get(0).getRulesetId()).getDescribe())).setAttribute("opacity","1");
            return setupBpListDetailedSingle(scorelist, primaryColor, doc, svgRoot,offset);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new LazybotRuntimeException("SVG 处理时出错");
        }
    }
    public static Document createScoreListDetailed(List<ScoreSequence> scorelist,PlayerInfoVO info, Integer offset) throws IOException
    {
        try
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/ScoreListDetailed.svg");
            Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(filePath.toFile().toURI().toString());
            Element svgRoot = doc.getDocumentElement();
            String totalHeight = String.valueOf(130 + 120 * scorelist.size());
            String primaryColor = CommonTool.hsvToHex(info.getPrimaryColor(),0.4f,1.0f);
            svgRoot.setAttribute("height", totalHeight);
            doc.getElementById("background").setAttribute("height", totalHeight);
            doc.getElementById("footer").setAttribute("transform", "translate(0," + 120 * (scorelist.size() - 1) + ")");
            doc.getElementById("requestTime").setTextContent(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            doc.getElementById("playername").setTextContent(info.getPlayerName());
            doc.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", info.getAvatarUrl());
            doc.getElementById("totalPp").setTextContent(String.valueOf(Math.round(info.getPerformancePoint())));
            doc.getElementById(OsuMode.getMode(scorelist.get(0).getRulesetId()).getDescribe()).setAttribute("fill",primaryColor);
            doc.getElementById("label-".concat(OsuMode.getMode(scorelist.get(0).getRulesetId()).getDescribe())).setAttribute("opacity","1");
            doc.getElementById("playernameLabel").setAttribute("fill", primaryColor);
            doc.getElementById("totalPpLabel").setAttribute("fill", primaryColor);
            return setupBpListDetailedSingle(scorelist, primaryColor, doc, svgRoot, offset);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new LazybotRuntimeException("SVG 处理时出错");
        }
    }

    public static Document createMapScoreList(List<MapScore> scorelist, BeatmapPerformance beatmap) throws IOException
    {
        try
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/MapScoresPanel.svg");
            Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(filePath.toFile().toURI().toString());
            Element svgRoot = doc.getDocumentElement();
            String totalHeight = String.valueOf(400 + 75 * scorelist.size());
            svgRoot.setAttribute("height", totalHeight);
            doc.getElementById("map-bg").setAttributeNS(xlinkns, "xlink:href", beatmap.getBgUrl());
            doc.getElementById("container").setAttribute("height", totalHeight);

            doc.getElementById("star-all-icon").setAttribute("fill","black");
            doc.getElementById("version").setTextContent(beatmap.getVersion());
            doc.getElementById("mapper").setTextContent(beatmap.getCreator());
            doc.getElementById("title").setTextContent(beatmap.getTitle());
            doc.getElementById("artist").setTextContent(beatmap.getArtist());

            doc.getElementById("length").setTextContent(CommonTool.formatHitLength(beatmap.getHit_length()));
            doc.getElementById("bpm").setTextContent(String.valueOf(Math.round(beatmap.getBpm())));
            doc.getElementById("playcount").setTextContent(CommonTool.formatNumber(beatmap.getPlayCount()));
            doc.getElementById("favourite").setTextContent(CommonTool.formatNumber(beatmap.getFavouriteCount()));

            doc.getElementById("max-combo").setTextContent(beatmap.getMax_combo()+"x");
            doc.getElementById("circles").setTextContent(String.valueOf(Optional.ofNullable(beatmap.getCountCircles()).orElse(0)));
            doc.getElementById("sliders").setTextContent(String.valueOf(Optional.ofNullable(beatmap.getCountSliders()).orElse(0)));
            doc.getElementById("spinners").setTextContent(String.valueOf(Optional.ofNullable(beatmap.getCountSpinners()).orElse(0)));


            int hue = CommonTool.rgbToHue(
                    CommonTool.hexToRgb(
                            CommonTool.calcDiffColor(beatmap.getDifficult_rating()
                            )
                    )
            );

            HSL lighterStar=new HSL(hue,97,70);
            HSL darkerStar=new HSL(hue,42,17);
            if (hue>360)
            {
                lighterStar=new HSL(hue,75,5);
                darkerStar=new HSL(hue,20,75);
            }
            switch (beatmap.getDifficultyAttributes()) {
                case OsuDifficultyAttributes osu -> {
                    doc.getElementById("mode-osu").setAttribute("opacity", "1");
                    doc.getElementById("mode-osu").setAttribute("fill", darkerStar.toString());
                    doc.getElementById("osu-stats-2").setAttribute("opacity", "1");
                    doc.getElementById("osu-stats-3").setAttribute("opacity", "1");
                    doc.getElementById("star-aim").setAttribute("opacity", "1");
                    doc.getElementById("star-spd").setAttribute("opacity", "1");
                    doc.getElementById("cs-osu").setTextContent(CommonTool.toString(beatmap.getCs(), 1));
                    doc.getElementById("ar-osu").setTextContent(CommonTool.toString(beatmap.getAr(), 1));
                    doc.getElementById("od-osu").setTextContent(CommonTool.toString(beatmap.getAccuracy(), 1));
                    doc.getElementById("hp-osu").setTextContent(CommonTool.toString(beatmap.getDrain(), 1));
                    doc.getElementById("star-aim-num").setTextContent(CommonTool.toString(osu.getAim()));
                    doc.getElementById("star-spd-num").setTextContent(CommonTool.toString(osu.getSpeed()));

                    doc.getElementById("aimstrain").setTextContent(String.valueOf(Math.round(osu.getAimDifficultStrainCount())));
                    doc.getElementById("speedstrain").setTextContent(String.valueOf(Math.round(osu.getSpeedDifficultStrainCount())));
                    doc.getElementById("sliderfactor").setTextContent(CommonTool.toString(osu.getSliderFactor() * 100).concat("%"));
                    doc.getElementById("lengthbonus").setTextContent(CommonTool.toString(beatmap.getLengthBonus(),3));
                }
                case TaikoDifficultyAttributes taiko -> {
                    doc.getElementById("mode-taiko").setAttribute("opacity", "1");
                    doc.getElementById("mode-taiko").setAttribute("fill", darkerStar.toString());
                    doc.getElementById("taiko-stats-2").setAttribute("opacity", "1");
                    doc.getElementById("taiko-stats-3").setAttribute("opacity", "1");
                    doc.getElementById("od-taiko").setTextContent(CommonTool.toString(beatmap.getAccuracy(), 1));
                    doc.getElementById("hp-taiko").setTextContent(CommonTool.toString(beatmap.getDrain(), 1));

                    doc.getElementById("stamina").setTextContent(CommonTool.toString(taiko.getStamina()));
                    doc.getElementById("rhythm").setTextContent(CommonTool.toString(taiko.getRhythm()));
                    doc.getElementById("color").setTextContent(CommonTool.toString(taiko.getColor()));
                    doc.getElementById("peak").setTextContent(CommonTool.toString(taiko.getPeak()*1000000).concat("^-6"));
                }
                case CatchDifficultyAttributes fruits -> {
                    doc.getElementById("mode-ctb").setAttribute("opacity", "1");
                    doc.getElementById("mode-ctb").setAttribute("fill", darkerStar.toString());
                    doc.getElementById("fruits-stats-2").setAttribute("opacity", "1");
                    doc.getElementById("fruits-stats-3").setAttribute("opacity", "1");
                    doc.getElementById("cs-fruits").setTextContent(CommonTool.toString(beatmap.getCs(), 1));
                    doc.getElementById("ar-fruits").setTextContent(CommonTool.toString(beatmap.getAr(), 1));
                    doc.getElementById("od-fruits").setTextContent(CommonTool.toString(beatmap.getAccuracy(), 1));
                    doc.getElementById("hp-fruits").setTextContent(CommonTool.toString(beatmap.getDrain(), 1));

                    doc.getElementById("fruits").setTextContent(String.valueOf(fruits.getNFruits()));
                    doc.getElementById("droplets").setTextContent(String.valueOf(fruits.getNDroplets()));
                    doc.getElementById("tinydroplets").setTextContent(String.valueOf(fruits.getNTinyDroplets()));
                    doc.getElementById("convert1").setTextContent(String.valueOf(beatmap.getConvert()));
                }
                case ManiaDifficultyAttributes mania -> {
                    doc.getElementById("mode-taiko").setAttribute("opacity", "1");
                    doc.getElementById("mode-taiko").setAttribute("fill", darkerStar.toString());
                    doc.getElementById("mania-stats-2").setAttribute("opacity", "1");
                    doc.getElementById("mania-stats-3").setAttribute("opacity", "1");
                    doc.getElementById("key-mania").setTextContent(CommonTool.toString(beatmap.getCs(), 1));
                    doc.getElementById("od-mania").setTextContent(CommonTool.toString(beatmap.getAccuracy(), 1));
                    doc.getElementById("hp-mania").setTextContent(CommonTool.toString(beatmap.getDrain(), 1));

                    doc.getElementById("objects").setTextContent(String.valueOf(mania.getNObjects()));
                    doc.getElementById("holdnotes").setTextContent(String.valueOf(mania.getNHoldNotes()));
                    doc.getElementById("convert2").setTextContent(String.valueOf(beatmap.getConvert()));
                }
            }

            doc.getElementById("bid").setTextContent(String.valueOf(beatmap.getBid()));
            doc.getElementById("sid").setTextContent(String.valueOf(beatmap.getSid()));
            doc.getElementById(beatmap.getStatus()).setAttribute("opacity", "1");
            doc.getElementById("star-all-num").setTextContent(CommonTool.toString(beatmap.getDifficult_rating()));



            doc.getElementById("star-all-num").setAttribute("fill", lighterStar.toString());
            doc.getElementById("star-aim-num").setAttribute("fill", lighterStar.toString());
            doc.getElementById("star-spd-num").setAttribute("fill", lighterStar.toString());
            doc.getElementById("star-all-icon").setAttribute("fill", lighterStar.toString());
            doc.getElementById("star-aim-icon").setAttribute("fill", lighterStar.toString());
            doc.getElementById("star-spd-icon").setAttribute("fill", lighterStar.toString());
            doc.getElementById("star-left-bg").setAttribute("fill", lighterStar.toString());
            doc.getElementById("map-stats-bg").setAttribute("fill", lighterStar.toString());

            doc.getElementById("star-all-bg").setAttribute("fill", darkerStar.toString());
            doc.getElementById("star-aim-bg").setAttribute("fill", darkerStar.toString());
            doc.getElementById("star-spd-bg").setAttribute("fill", darkerStar.toString());
            doc.getElementById("allversion").setAttribute("fill", darkerStar.toString());
            doc.getElementById("star-spd-label").setAttribute("fill", darkerStar.toString());
            doc.getElementById("star-aim-label").setAttribute("fill", darkerStar.toString());


            return setupAllScoreListElement(scorelist, doc, svgRoot, beatmap.getMode());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new LazybotRuntimeException("SVG 处理时出错");
        }
    }


    private static Document setupBpListDetailedSingle(List<ScoreSequence> scorelist, String primaryColor, Document doc, Element svgRoot, Integer offset)
    {
        int listIndex=0;
        for (ScoreSequence score : scorelist)
        {
            Node sectionFullNode = doc.createElementNS(namespaceSVG, "g");
            Element sectionFull = (Element) sectionFullNode;
            Node totalBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element totalBG = (Element) totalBGNode;
            totalBG.setAttribute("rx", "10");
            totalBG.setAttribute("ry", "10");
            totalBG.setAttribute("width", "950");
            totalBG.setAttribute("height", "100");
            totalBG.setAttribute("fill", "#2a2933");
            totalBG.setAttribute("transform", "translate(30,80)");

            Node mapBGImageNode = doc.createElementNS(namespaceSVG, "image");
            Element mapBGImage = (Element) mapBGImageNode;
            mapBGImage.setAttributeNS(xlinkns, "xlink:href", score.getBeatmap().getBgUrl());
            mapBGImage.setAttribute("x", "30");
            mapBGImage.setAttribute("y", "80");
            mapBGImage.setAttribute("width", "950");
            mapBGImage.setAttribute("height", "100");
            mapBGImage.setAttribute("opacity", "0.5");
            mapBGImage.setAttribute("clip-path", "url(#singleClip)");
            mapBGImage.setAttribute("preserveAspectRatio", "xMidYMid slice");

            Node totalBGMaskNode = doc.createElementNS(namespaceSVG, "rect");
            Element totalBGMask = (Element) totalBGMaskNode;
            totalBGMask.setAttribute("rx", "10");
            totalBGMask.setAttribute("ry", "10");
            totalBGMask.setAttribute("width", "950");
            totalBGMask.setAttribute("height", "100");
            totalBGMask.setAttribute("fill", "#2a2933");
            totalBGMask.setAttribute("opacity", "0.5");
            totalBGMask.setAttribute("transform", "translate(30,80)");

            Node playerNameNode = doc.createElementNS(namespaceSVG, "text");
            Element playerName = (Element) playerNameNode;
            playerName.setAttribute("class", "cls-122");
            playerName.setAttribute("transform", "translate(70 170)");
            playerName.setTextContent(score.getPlayerName());

            Node starAndSongTitleNode = doc.createElementNS(namespaceSVG, "text");
            Element starAndSongTitle = (Element) starAndSongTitleNode;
            starAndSongTitle.setAttribute("class", "cls-110");
            starAndSongTitle.setAttribute("transform", "translate(60 125)");

            Node starNode = doc.createElementNS(namespaceSVG, "tspan");
            Element star = (Element) starNode;
            star.setAttribute("fill", primaryColor);
            star.setTextContent(CommonTool.toString(score.getBeatmap().getDifficult_rating()).concat(" *"));

            Node divisorNode = doc.createElementNS(namespaceSVG, "tspan");
            Element divisor = (Element) divisorNode;
            divisor.setTextContent(" | ");

            Node titleNode = doc.createElementNS(namespaceSVG, "tspan");
            Element title = (Element) titleNode;
            title.setTextContent(score.getBeatmap().getArtist().concat(" - ").concat(score.getBeatmap().getTitle()));


            //pending
//            Node scoreStatusNode = doc.createElementNS(namespaceSVG, "tspan");
//            Element scoreStatus  = (Element) scoreStatusNode;
//            scoreStatus.setAttribute("fill", "#f8bad4");
//            scoreStatus.setTextContent(String.valueOf(score.getBeatmap().getDifficult_rating()).concat(" *"));

            starAndSongTitle.appendChild(star);
            starAndSongTitle.appendChild(divisor);
            starAndSongTitle.appendChild(title);

            Node bpmAndMapperNode = doc.createElementNS(namespaceSVG, "text");
            Element bpmAndMapper = (Element) bpmAndMapperNode;
            bpmAndMapper.setAttribute("class", "cls-113");
            bpmAndMapper.setAttribute("transform", "translate(60 150)");

            Node bpmNode = doc.createElementNS(namespaceSVG, "tspan");
            Element bpm = (Element) bpmNode;
            bpm.setAttribute("fill", primaryColor);
            bpm.setTextContent(String.valueOf(Math.round(score.getBeatmap().getBpm())).concat(" bpm"));

            Node divisorNode2 = doc.createElementNS(namespaceSVG, "tspan");
            Element divisor2 = (Element) divisorNode2;
            divisor2.setTextContent(" | ");

            Node mapperNode = doc.createElementNS(namespaceSVG, "tspan");
            Element mapper = (Element) mapperNode;
            mapper.setTextContent(score.getBeatmap().getCreator().concat(" // [").concat(score.getBeatmap().getVersion()).concat("]"));

            bpmAndMapper.appendChild(bpm);
            bpmAndMapper.appendChild(divisor2);
            bpmAndMapper.appendChild(mapper);

            Node underlineOfDateNode = doc.createElementNS(namespaceSVG, "rect");
            Element underlineOfDate = (Element) underlineOfDateNode;
            underlineOfDate.setAttribute("rx", "1.5");
            underlineOfDate.setAttribute("ry", "1.5");
            underlineOfDate.setAttribute("width", "105");
            underlineOfDate.setAttribute("height", "3");
            underlineOfDate.setAttribute("fill", primaryColor);
            underlineOfDate.setAttribute("transform", "translate(377.5,177)");

            Node underlineOfComboNode = doc.createElementNS(namespaceSVG, "rect");
            Element underlineOfCombo = (Element) underlineOfComboNode;
            underlineOfCombo.setAttribute("rx", "1.5");
            underlineOfCombo.setAttribute("ry", "1.5");
            underlineOfCombo.setAttribute("width", "55");
            underlineOfCombo.setAttribute("height", "3");
            underlineOfCombo.setAttribute("fill", primaryColor);
            underlineOfCombo.setAttribute("transform", "translate(518,177)");

            Node underlineOfAccuracyNode = doc.createElementNS(namespaceSVG, "rect");
            Element underlineOfAccuracy = (Element) underlineOfAccuracyNode;
            underlineOfAccuracy.setAttribute("rx", "1.5");
            underlineOfAccuracy.setAttribute("ry", "1.5");
            underlineOfAccuracy.setAttribute("width", "70");
            underlineOfAccuracy.setAttribute("height", "3");
            underlineOfAccuracy.setAttribute("fill", primaryColor);
            underlineOfAccuracy.setAttribute("transform", "translate(605,177)");

            Node underlineOfIndexNode = doc.createElementNS(namespaceSVG, "rect");
            Element underlineOfIndex = (Element) underlineOfIndexNode;
            underlineOfIndex.setAttribute("rx", "1.5");
            underlineOfIndex.setAttribute("ry", "1.5");
            underlineOfIndex.setAttribute("width", "40");
            underlineOfIndex.setAttribute("height", "3");
            underlineOfIndex.setAttribute("fill", primaryColor);
            underlineOfIndex.setAttribute("transform", "translate(707,177)");

            Node underlineOfRankNode = doc.createElementNS(namespaceSVG, "rect");
            Element underlineOfRank = (Element) underlineOfRankNode;
            underlineOfRank.setAttribute("rx", "1.5");
            underlineOfRank.setAttribute("ry", "1.5");
            underlineOfRank.setAttribute("width", "30");
            underlineOfRank.setAttribute("height", "3");
            underlineOfRank.setAttribute("fill", RankColor.fromString(score.getRank()).getDarkRankColorHEX());
            underlineOfRank.setAttribute("transform", "translate(60,80)");

            Node dateNode = doc.createElementNS(namespaceSVG, "text");
            Element date = (Element) dateNode;
            date.setAttribute("class", "cls-111");
            date.setAttribute("font-size", "18px");
            date.setAttribute("text-anchor", "middle");
            date.setAttribute("fill", "#ffffff");
            date.setAttribute("transform", "translate(430 170)");
            date.setTextContent(score.getAchievedTime());

            Node comboNode = doc.createElementNS(namespaceSVG, "text");
            Element combo = (Element) comboNode;
            combo.setAttribute("class", "cls-111");
            combo.setAttribute("font-size", "18px");
            combo.setAttribute("text-anchor", "middle");
            combo.setAttribute("fill", "#ffffff");
            combo.setAttribute("transform", "translate(545 170)");
            combo.setTextContent(score.getMaxCombo().toString().concat("x"));

            Node accuracyNode = doc.createElementNS(namespaceSVG, "text");
            Element accuracy = (Element) accuracyNode;
            accuracy.setAttribute("class", "cls-111");
            accuracy.setAttribute("font-size", "18px");
            accuracy.setAttribute("text-anchor", "middle");
            accuracy.setAttribute("fill", "#ffffff");
            accuracy.setAttribute("transform", "translate(640 170)");
            accuracy.setTextContent(CommonTool.toString(score.getAccuracy() * 100).concat("%"));

            Node indexNode = doc.createElementNS(namespaceSVG, "text");
            Element index = (Element) indexNode;
            index.setAttribute("class", "cls-111");
            index.setAttribute("font-size", "18px");
            index.setAttribute("text-anchor", "middle");
            index.setAttribute("fill", "#ffffff");
            index.setAttribute("transform", "translate(725 170)");
            index.setTextContent("#".concat(String.valueOf((score.getPositionInList()+offset))));

            Node ppNode = doc.createElementNS(namespaceSVG, "text");
            Element pp = (Element) ppNode;
            pp.setAttribute("class", "cls-114");
            pp.setAttribute("text-anchor", "end");
            pp.setAttribute("transform", "translate(960 142)");
            pp.setTextContent(String.valueOf(Math.round(score.getPp())).concat("pp"));

            Node differenceNode = doc.createElementNS(namespaceSVG, "text");
            Element difference = (Element) differenceNode;

            difference.setAttribute("transform", "translate(910 162)");
            difference.setAttribute("text-anchor", "middle");
            if(score.getDifferenceBetweenNextScore()>0)
            {
                difference.setAttribute("class", "cls-115");
                difference.setTextContent("+".concat(String.valueOf(score.getDifferenceBetweenNextScore())).concat("pp"));
            }
            else if(score.getDifferenceBetweenNextScore()<0)
            {
                difference.setAttribute("class", "cls-116");
                difference.setTextContent(String.valueOf(score.getDifferenceBetweenNextScore()).concat("pp"));
            }
            else
            {
                difference.setAttribute("class", "cls-117");
                difference.setTextContent("- pp");
            }

            sectionFull.appendChild(totalBG);
            sectionFull.appendChild(mapBGImage);
            sectionFull.appendChild(totalBGMask);
            sectionFull.appendChild(playerName);
            sectionFull.appendChild(starAndSongTitle);
            sectionFull.appendChild(bpmAndMapper);
            sectionFull.appendChild(underlineOfDate);
            sectionFull.appendChild(underlineOfCombo);
            sectionFull.appendChild(underlineOfAccuracy);
            sectionFull.appendChild(underlineOfIndex);
            sectionFull.appendChild(underlineOfRank);
            sectionFull.appendChild(date);
            sectionFull.appendChild(combo);
            sectionFull.appendChild(accuracy);
            sectionFull.appendChild(index);
            sectionFull.appendChild(pp);
            sectionFull.appendChild(difference);
            setupModIconForScoreListDetailed(score.getModList(), doc, sectionFull);
            sectionFull.setAttribute("transform", "translate(0," + 120 * listIndex + ")");
            svgRoot.appendChild(sectionFull);
            listIndex++;
        }
        return doc;
    }
    private static Document setupAllScoreListElement(List<MapScore> scorelist, Document doc, Element svgRoot, OsuMode mode) throws Exception
    {
        int listIndex=0;
        for (MapScore score : scorelist) {
            Node sectionFullNode = doc.createElementNS(namespaceSVG, "g");
            Element sectionFull = (Element) sectionFullNode;

            Node totalBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element totalBG = (Element) totalBGNode;
            totalBG.setAttribute("rx", "10");
            totalBG.setAttribute("x", "30");
            totalBG.setAttribute("y", "398");
            totalBG.setAttribute("width", "740");
            totalBG.setAttribute("height", "60");
            totalBG.setAttribute("fill", RankColor.fromString(score.getRank()).getBackgroundColorPeppyHEX());

            Node leftBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element leftBG = (Element) leftBGNode;
            leftBG.setAttribute("rx", "10");
            leftBG.setAttribute("x", "30");
            leftBG.setAttribute("y", "398");
            leftBG.setAttribute("width", "717");
            leftBG.setAttribute("height", "60");
            leftBG.setAttribute("fill", "#414141");

            Node borderNode = doc.createElementNS(namespaceSVG, "rect");
            Element borderBG = (Element) borderNode;
            borderBG.setAttribute("rx", "10");
            borderBG.setAttribute("x", "32");
            borderBG.setAttribute("y", "399");
            borderBG.setAttribute("width", "715");
            borderBG.setAttribute("height", "58");
            borderBG.setAttribute("stroke", "url(#rank-border-"+score.getRank()+")");
            borderBG.setAttribute("stroke-width", "2");
            borderBG.setAttribute("fill", "none");

            Node gradNode = doc.createElementNS(namespaceSVG, "rect");
            Element gradBG = (Element) gradNode;
            gradBG.setAttribute("rx", "10");
            gradBG.setAttribute("x", "30");
            gradBG.setAttribute("y", "398");
            gradBG.setAttribute("width", "717");
            gradBG.setAttribute("height", "60");
            gradBG.setAttribute("fill", "url(#rank-filler-"+score.getRank()+")");
            gradBG.setAttribute("fill-opacity", "0.5");

            Node rankGroupNode = doc.createElementNS(namespaceSVG, "g");
            Element rankGroup = (Element) rankGroupNode;
            rankGroup.setAttribute("clip-path", "url(#rankClip)");

            Node rankTextNode = doc.createElementNS(namespaceSVG, "text");
            Element rankText = (Element) rankTextNode;
            rankText.setAttribute("class", "cls-3");
            rankText.setAttribute("transform", "rotate(-30,753,450)");
            rankText.setAttribute("x", "753");
            rankText.setAttribute("y", "450");
            rankText.setAttribute("font-weight", "700");
            rankText.setAttribute("font-size", "38px");
            rankText.setAttribute("fill", RankColor.fromString(score.getRank()).getIconColorPeppyHEX());
            rankText.setTextContent(score.getRank().substring(0,1));

            rankGroup.appendChild(rankText);

            Node bgDimNode = doc.createElementNS(namespaceSVG, "rect");
            Element bgDim = (Element) bgDimNode;
            bgDim.setAttribute("rx", "8");
            bgDim.setAttribute("x", "54");
            bgDim.setAttribute("y", "400");
            bgDim.setAttribute("width", "691");
            bgDim.setAttribute("height", "56");
            bgDim.setAttribute("fill-opacity", "0.3");


            HSL ppColor=new HSL(CommonTool.rgbToHue(CommonTool.hexToRgb(
                    RankColor.fromString(score.getRank()).getBackgroundColorPeppyHEX().substring(1))),
                    41,80);
            Node ppNode = doc.createElementNS(namespaceSVG, "text");
            Element pp = (Element) ppNode;
            pp.setAttribute("class", "cls-1");
            pp.setAttribute("x", "735");
            pp.setAttribute("y", "435");
            pp.setAttribute("font-size", "20px");
            pp.setAttribute("text-anchor", "end");
            pp.setAttribute("font-weight", "600");
            pp.setAttribute("fill",ppColor.toString());
            pp.setTextContent(String.valueOf(Math.round(Optional.ofNullable(score.getPp()).orElse(0.0))).concat("pp"));

            Node iffcNode = doc.createElementNS(namespaceSVG, "text");
            Element iffc = (Element) iffcNode;
            iffc.setAttribute("class", "cls-1");
            iffc.setAttribute("x", "735");
            iffc.setAttribute("y", "448");
            iffc.setAttribute("font-size", "10px");
            iffc.setAttribute("text-anchor", "end");
            iffc.setAttribute("opacity", "0.9");
            iffc.setAttribute("fill",ppColor.toString());
            if (score.getIsPerfectCombo())
                iffc.setAttribute("opacity", "0.5");

            Node iffcLabelNode = doc.createElementNS(namespaceSVG, "tspan");
            Element iffcLabel = (Element) iffcLabelNode;
            iffcLabel.setTextContent("if fc ");

            Node iffcNumberNode = doc.createElementNS(namespaceSVG, "tspan");
            Element iffcNumber = (Element) iffcNumberNode;
            iffcNumber.setAttribute("font-weight", "600");
            iffcNumber.setTextContent(Math.round(Optional.ofNullable(score.getIffc()).orElse(0.0))+"pp");

            iffc.appendChild(iffcLabel);
            iffc.appendChild(iffcNumber);


            Node divisorNode = doc.createElementNS(namespaceSVG, "rect");
            Element divisor = (Element) divisorNode;
            divisor.setAttribute("rx", "10");
            divisor.setAttribute("x", "30");
            divisor.setAttribute("y", "398");
            divisor.setAttribute("width", "617");
            divisor.setAttribute("height", "60");
            divisor.setAttribute("fill", "#262626");

            Node playerBGImageNode = doc.createElementNS(namespaceSVG, "image");
            Element playerBGImage = (Element) playerBGImageNode;
            playerBGImage.setAttributeNS(xlinkns, "xlink:href", score.getBannerUrl());
            playerBGImage.setAttribute("x", "74");
            playerBGImage.setAttribute("y", "398");
            playerBGImage.setAttribute("width", "570");
            playerBGImage.setAttribute("height", "60");
            playerBGImage.setAttribute("opacity", "0.3");
            playerBGImage.setAttribute("clip-path", "url(#bannerClip)");
            playerBGImage.setAttribute("preserveAspectRatio", "xMidYMid slice");

            Node totalBGMaskNode = doc.createElementNS(namespaceSVG, "rect");
            Element totalBGMask = (Element) totalBGMaskNode;
            totalBGMask.setAttribute("rx", "10");
            totalBGMask.setAttribute("x", "74");
            totalBGMask.setAttribute("y", "398");
            totalBGMask.setAttribute("width", "570");
            totalBGMask.setAttribute("height", "60");
            totalBGMask.setAttribute("fill-opacity", "0.25");

            Node gradGrayNode = doc.createElementNS(namespaceSVG, "rect");
            Element gradGray = (Element) gradGrayNode;
            gradGray.setAttribute("rx", "10");
            gradGray.setAttribute("x", "54");
            gradGray.setAttribute("y", "398");
            gradGray.setAttribute("width", "590");
            gradGray.setAttribute("height", "60");
            gradGray.setAttribute("fill", "url(#gray-1)");

            Node playerNameNode = doc.createElementNS(namespaceSVG, "text");
            Element playerName = (Element) playerNameNode;
            playerName.setAttribute("class", "cls-1");
            playerName.setAttribute("x", "104");
            playerName.setAttribute("y", "423");
            playerName.setAttribute("font-weight", "600");
            playerName.setAttribute("font-size", "18px");
            playerName.setAttribute("fill", "#ffffff");
            playerName.setTextContent(score.getPlayerName());

            Node timeNode = doc.createElementNS(namespaceSVG, "text");
            Element time = (Element) timeNode;
            time.setAttribute("class", "cls-1");
            time.setAttribute("x", "104");
            time.setAttribute("y", "435");
            time.setAttribute("font-size", "8px");
            time.setAttribute("fill", "#ffffff");
            time.setTextContent(convertDate(score.getAchievedTime()));

            Node indexNode = doc.createElementNS(namespaceSVG, "text");
            Element index = (Element) indexNode;
            index.setAttribute("class", "cls-1");
            index.setAttribute("x", "104");
            index.setAttribute("y", "448");
            index.setAttribute("font-weight", "600");
            index.setAttribute("font-size", "9px");
            index.setAttribute("fill", "#F3F3F3");
            index.setAttribute("opacity", "1");
            index.setTextContent(CommonTool.toString(score.getStarRating())+"* | " + Math.round(score.getBpm()) + " bpm | " + "#" + (listIndex+1));

            Node accuracyLabelNode = doc.createElementNS(namespaceSVG, "text");
            Element accuracyLabel = (Element) accuracyLabelNode;
            accuracyLabel.setAttribute("class", "cls-1");
            accuracyLabel.setAttribute("x", "505");
            accuracyLabel.setAttribute("y", "412");
            accuracyLabel.setAttribute("font-size", "6px");
            accuracyLabel.setAttribute("fill", "#B9C1C6");
            accuracyLabel.setTextContent("Accuracy");

            Node ComboLabelNode = doc.createElementNS(namespaceSVG, "text");
            Element ComboLabel = (Element) ComboLabelNode;
            ComboLabel.setAttribute("class", "cls-1");
            ComboLabel.setAttribute("x", "582");
            ComboLabel.setAttribute("y", "412");
            ComboLabel.setAttribute("font-size", "6px");
            ComboLabel.setAttribute("fill", "#B9C1C6");
            ComboLabel.setTextContent("Combo");

            Node scoreLabelNode = doc.createElementNS(namespaceSVG, "text");
            Element scoreLabel = (Element) scoreLabelNode;
            scoreLabel.setAttribute("class", "cls-1");
            scoreLabel.setAttribute("x", "425");
            scoreLabel.setAttribute("y", "412");
            scoreLabel.setAttribute("font-size", "6px");
            scoreLabel.setAttribute("fill", "#B9C1C6");
            scoreLabel.setTextContent("Score");

            Node accuracyNode = doc.createElementNS(namespaceSVG, "text");
            Element accuracy = (Element) accuracyNode;
            accuracy.setAttribute("class", "cls-1");
            accuracy.setAttribute("x", "505");
            accuracy.setAttribute("y", "427");
            accuracy.setAttribute("font-size", "15px");
            accuracy.setAttribute("fill", accuracyColorMid(score.getAccuracy()));
            accuracy.setTextContent(CommonTool.toString(score.getAccuracy() * 100).concat("%"));

            Node comboNode = doc.createElementNS(namespaceSVG, "text");
            Element combo = (Element) comboNode;
            combo.setAttribute("class", "cls-1");
            combo.setAttribute("x", "582");
            combo.setAttribute("y", "427");
            combo.setAttribute("font-size", "15px");
            combo.setAttribute("fill", "#ffffff");
            if (score.getIsPerfectCombo())
                combo.setAttribute("fill", "#B9FD9B");
            combo.setTextContent(score.getMaxCombo()+"x");

            Node scoreNode = doc.createElementNS(namespaceSVG, "text");
            Element totalScore = (Element) scoreNode;
            totalScore.setAttribute("class", "cls-1");
            totalScore.setAttribute("x", "425");
            totalScore.setAttribute("y", "427");
            totalScore.setAttribute("font-size", "15px");
            totalScore.setAttribute("fill", "#ffffff");
            if (score.getIsPerfectCombo())
                totalScore.setAttribute("fill", "#B9FD9B");
            totalScore.setTextContent(NumberFormat.getNumberInstance(Locale.US).format(score.getScore()));

            Node avatarNode = doc.createElementNS(namespaceSVG, "image");
            Element avatar = (Element) avatarNode;
            avatar.setAttributeNS(xlinkns, "xlink:href", score.getAvatarUrl());
            avatar.setAttribute("preserveAspectRatio", "xMidYMid slice");
            avatar.setAttribute("x", "30");
            avatar.setAttribute("y", "398");
            avatar.setAttribute("width", "60");
            avatar.setAttribute("height", "60");
            avatar.setAttribute("clip-path", "url(#avatarClip)");


            sectionFull.appendChild(totalBG);
            sectionFull.appendChild(leftBG);
            sectionFull.appendChild(borderBG);
            sectionFull.appendChild(gradBG);
            sectionFull.appendChild(rankGroup);
            sectionFull.appendChild(bgDim);
            sectionFull.appendChild(divisor);
            sectionFull.appendChild(playerBGImage);
            sectionFull.appendChild(totalBGMask);
            sectionFull.appendChild(gradGray);
            sectionFull.appendChild(playerName);
            sectionFull.appendChild(time);
            sectionFull.appendChild(index);
            sectionFull.appendChild(pp);
            sectionFull.appendChild(iffc);
            sectionFull.appendChild(avatar);
            sectionFull.appendChild(accuracyLabel);
            sectionFull.appendChild(ComboLabel);
            sectionFull.appendChild(scoreLabel);
            sectionFull.appendChild(accuracy);
            sectionFull.appendChild(combo);
            sectionFull.appendChild(totalScore);


            setupAllScoreLabelsMode(doc, score, sectionFull, mode);
            setupModIconForAllScores(score.getModList(), doc, sectionFull);
            sectionFull.setAttribute("transform", "translate(0," + 75 * listIndex + ")");
            svgRoot.appendChild(sectionFull);
            listIndex++;
        }
        return doc;
    }

    private static void setupAllScoreLabelsMode(Document doc, MapScore score, Element sectionFull, OsuMode mode)
    {
        Node label300Node = doc.createElementNS(namespaceSVG, "text");
        Element label300 = (Element) label300Node;
        label300.setAttribute("class", "cls-1");
        label300.setAttribute("x", "485");
        label300.setAttribute("y", "437.8");
        label300.setAttribute("font-size", "6px");
        label300.setAttribute("fill", "#B9C1C6");

        Node label100Node = doc.createElementNS(namespaceSVG, "text");
        Element label100 = (Element) label100Node;
        label100.setAttribute("class", "cls-1");
        label100.setAttribute("x", "521");
        label100.setAttribute("y", "437.8");
        label100.setAttribute("font-size", "6px");
        label100.setAttribute("fill", "#B9C1C6");

        Node label50Node = doc.createElementNS(namespaceSVG, "text");
        Element label50 = (Element) label50Node;
        label50.setAttribute("class", "cls-1");
        label50.setAttribute("x", "551");
        label50.setAttribute("y", "437.8");
        label50.setAttribute("font-size", "6px");
        label50.setAttribute("fill", "#B9C1C6");

        Node labelMissNode = doc.createElementNS(namespaceSVG, "text");
        Element labelMiss = (Element) labelMissNode;
        labelMiss.setAttribute("class", "cls-1");
        labelMiss.setAttribute("x", "579");
        labelMiss.setAttribute("y", "437.8");
        labelMiss.setAttribute("font-size", "6px");
        labelMiss.setAttribute("fill", "#B9C1C6");

        Node countOf300Node = doc.createElementNS(namespaceSVG, "text");
        Element countOf300 = (Element) countOf300Node;
        countOf300.setAttribute("class", "cls-1");
        countOf300.setAttribute("x", "485");
        countOf300.setAttribute("y", "450");
        countOf300.setAttribute("font-size", "12px");
        countOf300.setAttribute("fill", "#ffffff");

        Node countOf100Node = doc.createElementNS(namespaceSVG, "text");
        Element countOf100 = (Element) countOf100Node;
        countOf100.setAttribute("class", "cls-1");
        countOf100.setAttribute("x", "521");
        countOf100.setAttribute("y", "450");
        countOf100.setAttribute("font-size", "12px");
        countOf100.setAttribute("fill", "#ffffff");

        Node countOf50Node = doc.createElementNS(namespaceSVG, "text");
        Element countOf50 = (Element) countOf50Node;
        countOf50.setAttribute("class", "cls-1");
        countOf50.setAttribute("x", "551");
        countOf50.setAttribute("y", "450");
        countOf50.setAttribute("font-size", "12px");
        countOf50.setAttribute("fill", "#ffffff");

        Node countOfMissNode = doc.createElementNS(namespaceSVG, "text");
        Element countOfMiss = (Element) countOfMissNode;
        countOfMiss.setAttribute("class", "cls-1");
        countOfMiss.setAttribute("x", "579");
        countOfMiss.setAttribute("y", "450");
        countOfMiss.setAttribute("font-size", "12px");
        countOfMiss.setAttribute("fill", "#ffffff");

        if (mode == OsuMode.Osu) {
            countOfMiss.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getMiss()).orElse(0)));
            countOf50.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getMeh()).orElse(0)));
            countOf100.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getOk()).orElse(0)));
            countOf300.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getGreat()).orElse(0)));
            labelMiss.setTextContent("Miss");
            label50.setTextContent("50");
            label100.setTextContent("100");
            label300.setTextContent("300");
            if (score.getIsLazer())
            {
                Node labelTickNode = doc.createElementNS(namespaceSVG, "text");
                Element labelTick = (Element) labelTickNode;
                labelTick.setAttribute("class", "cls-1");
                labelTick.setAttribute("x", "410");
                labelTick.setAttribute("y", "437.8");
                labelTick.setAttribute("font-size", "6px");
                labelTick.setAttribute("fill", "#B9C1C6");
                labelTick.setAttribute("opacity", "0.6");
                labelTick.setTextContent("Tick");

                Node labelEndNode = doc.createElementNS(namespaceSVG, "text");
                Element labelEnd = (Element) labelEndNode;
                labelEnd.setAttribute("class", "cls-1");
                labelEnd.setAttribute("x", "446");
                labelEnd.setAttribute("y", "437.8");
                labelEnd.setAttribute("font-size", "6px");
                labelEnd.setAttribute("fill", "#B9C1C6");
                labelEnd.setAttribute("opacity", "0.6");
                labelEnd.setTextContent("End");

                Node countOfTickNode = doc.createElementNS(namespaceSVG, "text");
                Element countOfTick = (Element) countOfTickNode;
                countOfTick.setAttribute("class", "cls-1");
                countOfTick.setAttribute("x", "410");
                countOfTick.setAttribute("y", "450");
                countOfTick.setAttribute("font-size", "12px");
                countOfTick.setAttribute("fill", "#ffffff");
                countOfTick.setAttribute("opacity", "0.6");
                countOfTick.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getLarge_tick_hit()).orElse(0)));

                Node countOfEndNode = doc.createElementNS(namespaceSVG, "text");
                Element countOfEnd = (Element) countOfEndNode;
                countOfEnd.setAttribute("class", "cls-1");
                countOfEnd.setAttribute("x", "446");
                countOfEnd.setAttribute("y", "450");
                countOfEnd.setAttribute("font-size", "12px");
                countOfEnd.setAttribute("fill", "#ffffff");
                countOfEnd.setAttribute("opacity", "0.6");
                countOfEnd.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getSlider_tail_hit()).orElse(0)));
                sectionFull.appendChild(labelTick);
                sectionFull.appendChild(labelEnd);
                sectionFull.appendChild(countOfEnd);
                sectionFull.appendChild(countOfTick);
            }
        }
        else if (mode == OsuMode.Taiko) {
            countOfMiss.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getLarge_bonus()).orElse(0)));
            countOf50.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getMiss()).orElse(0)));
            countOf100.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getOk()).orElse(0)));
            countOf300.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getGreat()).orElse(0)));
            labelMiss.setTextContent("Bonus");
            label50.setTextContent("Miss");
            label100.setTextContent("OK");
            label300.setTextContent("Great");
        }
        else if (mode == OsuMode.Catch) {
            label300.setAttribute("x", "475");
            label100.setAttribute("x", "511");
            label50.setAttribute("x", "541");
            countOf300.setAttribute("x", "475");
            countOf100.setAttribute("x", "511");
            countOf50.setAttribute("x", "541");
            countOfMiss.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getMiss()).orElse(0)));
            countOf50.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getSmall_tick_miss()).orElse(0)));
            countOf100.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getLarge_tick_hit()).orElse(0)));
            countOf300.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getGreat()).orElse(0)));
            labelMiss.setTextContent("Miss");
            label50.setTextContent("Drop Miss");
            label100.setTextContent("Ticks");
            label300.setTextContent("Fruits");
        }
        else {
            Node labelGreatNode = doc.createElementNS(namespaceSVG, "text");
            Element labelGreat = (Element) labelGreatNode;
            labelGreat.setAttribute("class", "cls-1");
            labelGreat.setAttribute("x", "400");
            labelGreat.setAttribute("y", "437.8");
            labelGreat.setAttribute("font-size", "6px");
            labelGreat.setAttribute("fill", "#B9C1C6");

            Node labelPerfectNode = doc.createElementNS(namespaceSVG, "text");
            Element labelPerfect = (Element) labelPerfectNode;
            labelPerfect.setAttribute("class", "cls-1");
            labelPerfect.setAttribute("x", "440");
            labelPerfect.setAttribute("y", "437.8");
            labelPerfect.setAttribute("font-size", "6px");
            labelPerfect.setAttribute("fill", "#B9C1C6");

            Node countOfPerfectNode = doc.createElementNS(namespaceSVG, "text");
            Element countOfPerfect = (Element) countOfPerfectNode;
            countOfPerfect.setAttribute("class", "cls-1");
            countOfPerfect.setAttribute("x", "400");
            countOfPerfect.setAttribute("y", "450");
            countOfPerfect.setAttribute("font-size", "12px");
            countOfPerfect.setAttribute("fill", "#ffffff");

            Node countOfGreatNode = doc.createElementNS(namespaceSVG, "text");
            Element countOfGreat = (Element) countOfGreatNode;
            countOfGreat.setAttribute("class", "cls-1");
            countOfGreat.setAttribute("x", "440");
            countOfGreat.setAttribute("y", "450");
            countOfGreat.setAttribute("font-size", "12px");
            countOfGreat.setAttribute("fill", "#ffffff");

            label300.setAttribute("x", "475");
            label100.setAttribute("x", "515");
            label50.setAttribute("x", "548");
            countOf300.setAttribute("x", "475");
            countOf100.setAttribute("x", "515");
            countOf50.setAttribute("x", "548");
            countOfMiss.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getMiss()).orElse(0)));
            countOf50.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getMeh()).orElse(0)));
            countOf100.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getOk()).orElse(0)));
            countOf300.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getGood()).orElse(0)));
            countOfGreat.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getGreat()).orElse(0)));
            countOfPerfect.setTextContent(String.valueOf(Optional.ofNullable(score.getStatistics().getPerfect()).orElse(0)));
            labelMiss.setTextContent("Miss");
            label50.setTextContent("Meh");
            label100.setTextContent("OK");
            label300.setTextContent("Good");
            labelGreat.setTextContent("Great");
            labelPerfect.setTextContent("Perfect");
            sectionFull.appendChild(labelPerfect);
            sectionFull.appendChild(labelGreat);
            sectionFull.appendChild(countOfGreat);
            sectionFull.appendChild(countOfPerfect);
        }
        sectionFull.appendChild(label300);
        sectionFull.appendChild(label100);
        sectionFull.appendChild(label50);
        sectionFull.appendChild(labelMiss);
        sectionFull.appendChild(countOf300);
        sectionFull.appendChild(countOf100);
        sectionFull.appendChild(countOfMiss);
        sectionFull.appendChild(countOf50);

    }







    private static Document setupModIconForScoreListDetailed(List<Mod> modList,Document doc,Element sectionFull)
    {
        if (modList.isEmpty()) return doc;
        modList=modList.reversed();
        for(int i=0;i<modList.size();i++)
        {
            Node modSingleNode = doc.createElementNS(namespaceSVG, "g");
            Element modSingle = (Element) modSingleNode;
            Node rectBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element rectBG = (Element) rectBGNode;
            rectBG.setAttribute("transform", "translate(925 95)");
            rectBG.setAttribute("rx", "7.5");
            rectBG.setAttribute("ry", "7.5");
            rectBG.setAttribute("width", "30");
            rectBG.setAttribute("height", "15");
            rectBG.setAttribute("fill", ModColor.getModTypeColorHEX(modList.get(i)));

            Node modAcronymNode = doc.createElementNS(namespaceSVG, "text");
            Element modAcronym = (Element) modAcronymNode;
            modAcronym.setAttribute("class", "cls-112");
            modAcronym.setAttribute("transform", "translate(940 107)");
            modAcronym.setAttribute("text-anchor", "middle");
            modAcronym.setTextContent(modList.get(i).getAcronym());

            modSingle.appendChild(rectBG);
            modSingle.appendChild(modAcronym);
            modSingle.setAttribute("transform", "translate(" + -35*i  + " 0)");
            sectionFull.appendChild(modSingleNode);
        }
        return doc;
    }
    private static Document setupModIconForAllScores(List<Mod> modList,Document doc,Element sectionFull)
    {
        if (modList.isEmpty()) return doc;
        modList=modList.stream().filter(mod -> !mod.getAcronym().equals("CL")).toList().reversed();
        for(int i=0;i<modList.size();i++)
        {
            Node modSingleNode = doc.createElementNS(namespaceSVG, "g");
            Element modSingle = (Element) modSingleNode;
            Node rectBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element rectBG = (Element) rectBGNode;
            rectBG.setAttribute("rx", "5");
            rectBG.setAttribute("x", "717");
            rectBG.setAttribute("y", "408");
            rectBG.setAttribute("width", "19");
            rectBG.setAttribute("height", "10");
            rectBG.setAttribute("fill", ModColor.fromString(modList.get(i).getAcronym()).getDetailedPrimaryColor().toString());

            Node modAcronymNode = doc.createElementNS(namespaceSVG, "text");
            Element modAcronym = (Element) modAcronymNode;
            modAcronym.setAttribute("class", "cls-4");
            modAcronym.setAttribute("x", "726.5");
            modAcronym.setAttribute("y", "416");
            modAcronym.setAttribute("text-anchor", "middle");
            modAcronym.setAttribute("font-size", "8px");
            modAcronym.setAttribute("fill", "#ffffff");
            modAcronym.setAttribute("font-weight", "600");
            modAcronym.setTextContent(modList.get(i).getAcronym());
            if(i>3)
            {
                rectBG.setAttribute("fill","#1f1e26");
                modAcronym.setTextContent("...");
                modAcronym.setAttribute("x", "722.8");
                modAcronym.setAttribute("text-anchor", "start");
                modAcronym.setAttribute("y", "413.5");
                modSingle.appendChild(rectBG);
                modSingle.appendChild(modAcronym);
                modSingle.setAttribute("transform", "translate(-66 0)");
                sectionFull.appendChild(modSingleNode);
                break;
            }
            modSingle.appendChild(rectBG);
            modSingle.appendChild(modAcronym);
            modSingle.setAttribute("transform", "translate(" + (-22*i)  + " 0)");
            sectionFull.appendChild(modSingleNode);
        }
        return doc;
    }

    public static Document createBpCard(PlayerInfoVO player, List<ScoreVO> scoreArray, Integer offset, Integer type, String infoMsg) throws IOException{
        URI inputUri;
        int targetHeight=0;
        if(type==0)
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/scoreListNew.svg");
            inputUri = filePath.toFile().toURI();
            targetHeight = 450 + 350 * ((int) (scoreArray.size() / 3));
            if (scoreArray.size() % 3 == 0)
                targetHeight -= 350;
        }
        else if(type==2||type==3||type==4)
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/NoChokeScoreCards.svg");
            inputUri = filePath.toFile().toURI();
            targetHeight = 450 + 270 * ((int) (scoreArray.size() / 3));
            if (scoreArray.size() % 3 == 0)
                targetHeight -= 270;
        }
        else
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/scoreListNewTrimed.svg");
            inputUri = filePath.toFile().toURI();
            targetHeight = 380 + 270 * ((int) (scoreArray.size() / 3));
            if (scoreArray.size() % 3 == 0)
                targetHeight -= 270;
        }
        Document document = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
        Element svgRoot = document.getDocumentElement();

        svgRoot.setAttribute("height", String.valueOf(targetHeight));
        document.getElementById(OsuMode.getMode(scoreArray.get(0).getMode()).getDescribe()).setAttribute("class", "cls-24");
        document.getElementById("background").setAttribute("height", String.valueOf(targetHeight));
        document.getElementById("playername").setTextContent(player.getPlayerName());
        if(type==2) {
            document.getElementById("totalPp").setTextContent(player.getFixedPPString());
        }
        else if(type==3) {
            document.getElementById("totalPp").setTextContent(player.getFixedPPString());
            if(infoMsg!=null) {
                document.getElementById("desc").setTextContent(infoMsg);
            }
        }
        else {
            document.getElementById("totalPp").setTextContent(String.valueOf(Math.round(player.getPerformancePoint())));
            if(type==4) {
                if(infoMsg!=null) {
                    document.getElementById("desc").setTextContent(infoMsg);
                }
            }
        }

        Element imageElement = document.getElementById("avatar");
        if (player.getAvatarUrl()!= null) {
            imageElement.setAttributeNS(xlinkns, "xlink:href", player.getAvatarUrl());
        }

        for(int i=0;i<scoreArray.size();i++)
        {
            SvgUtil.wireBpListCard(document,scoreArray.get(i),i,scoreArray.size(),offset,type);
        }
        return document;
    }
    public static Document createBpCard(PlayerInfoVO player, List<ScoreVO> scoreArray, Integer offset, Integer type) throws IOException
    {
       return createBpCard(player,scoreArray,offset,type,null);
    }
    private static void wireBpListCard(Document document, ScoreVO scoreVO, int index, int total, int offset, int type)
    {
        Element svgRoot = document.getDocumentElement();
        Node sectionFullNode = document.createElementNS(namespaceSVG, "g");
        Element sectionFull = (Element) sectionFullNode;
        Element defs = document.createElementNS(namespaceSVG, "defs");

        Node listSubSectionNode = document.createElementNS(namespaceSVG, "rect");
        Element listSubSection = (Element) listSubSectionNode;
        listSubSection.setAttribute("class", "cls-31");
        if(type==0)
        {
            listSubSection.setAttribute("x", "90");
            listSubSection.setAttribute("y", "90");
            listSubSection.setAttribute("width", "320");
            listSubSection.setAttribute("height", "320");
        }
        else {
            listSubSection.setAttribute("x", "40");
            listSubSection.setAttribute("y", "80");
            listSubSection.setAttribute("width", "240");
            listSubSection.setAttribute("height", "240");
        }

        Node mapBGNode = document.createElementNS(namespaceSVG, "image");
        Element mapBG = (Element) mapBGNode;
        mapBG.setAttributeNS(xlinkns, "xlink:href", scoreVO.getBeatmap().getBgUrl());
        mapBG.setAttribute("preserveAspectRatio", "xMidYMid slice");
        mapBG.setAttribute("opacity", "0.5");
        if(type==0) {
            mapBG.setAttribute("x", "90");
            mapBG.setAttribute("y", "90");
            mapBG.setAttribute("width", "320");
            mapBG.setAttribute("height", "320");
        }
        else {
            mapBG.setAttribute("x", "40");
            mapBG.setAttribute("y", "80");
            mapBG.setAttribute("width", "240");
            mapBG.setAttribute("height", "240");
        }


        Node listSubSectionNodeOpacity = document.createElementNS(namespaceSVG, "rect");
        Element listSubSectionOpacity  = (Element) listSubSectionNodeOpacity ;
        listSubSectionOpacity .setAttribute("class", "cls-31");
        listSubSectionOpacity.setAttribute("opacity", "0.7");
        if(type==0) {
            listSubSectionOpacity.setAttribute("x", "90");
            listSubSectionOpacity.setAttribute("y", "90");
            listSubSectionOpacity.setAttribute("width", "320");
            listSubSectionOpacity.setAttribute("height", "320");
        }
        else {
            listSubSectionOpacity.setAttribute("x", "40");
            listSubSectionOpacity.setAttribute("y", "80");
            listSubSectionOpacity.setAttribute("width", "240");
            listSubSectionOpacity.setAttribute("height", "240");
        }

        Node songTitleNode = document.createElementNS(namespaceSVG, "text");
        Element songTitle = (Element) songTitleNode;
        String title = scoreVO.getBeatmap().getTitle();
        if (title.length() >= 20) {
            title = title.substring(0, 19) + "...";
        }
        songTitle.setAttribute("class", "cls-5");
        if(type==0) {
            songTitle.setAttribute("transform", "translate(114 230)");
        }
        else {
            songTitle.setAttribute("transform", "translate(57 190)");
        }
        songTitle.setTextContent(title);


        String version = scoreVO.getBeatmap().getVersion();
        if (version.length() >= 35)
        {
            version = version.substring(0, 34) + "...";
        }
        Node difficultyNode = document.createElementNS(namespaceSVG, "text");
        Element difficulty = (Element) difficultyNode;
        difficulty.setAttribute("class", "cls-1");
        if(type==0)
        {
            difficulty.setAttribute("transform", "translate(115 280)");
        }
        else {
            difficulty.setAttribute("transform", "translate(57 230)");
        }
        difficulty.setTextContent("["+version+"]");


        String diffColor="#".concat(CommonTool.calcDiffColor(scoreVO.getBeatmap().getDifficult_rating()));
        String diffTextColor = "#fed867";
        String starType = "assets/osuResources/star-golden.svg";
        if (scoreVO.getBeatmap().getDifficult_rating() < 7.0) {
            if (scoreVO.getBeatmap().getDifficult_rating() % 1.0 < 0.5) {
                diffTextColor = "#1c1719";
                starType = "assets/osuResources/star-dark.svg";
            }
        }
        Node starNode = document.createElementNS(namespaceSVG, "text");
        Element star = (Element) starNode;
        star.setAttribute("class", "cls-46");
        if(type==0) {
            star.setAttribute("transform", "translate(398 408)");
        }
        else {
            star.setAttribute("transform", "translate(268 327.9)");
        }
        star.setAttribute("text-anchor", "middle");
        star.setAttribute("fill", diffTextColor);
        star.setTextContent(CommonTool.toString(scoreVO.getBeatmap().getDifficult_rating()));

        Node starImageNode = document.createElementNS(namespaceSVG, "image");
        Element starImage = (Element) starImageNode;
        starImage.setAttributeNS(xlinkns, "xlink:href", starType);
        if(type==0) {
            starImage.setAttribute("transform", "translate(380 401)");
        }
        else {
            starImage.setAttribute("transform", "translate(250 321.2)");
        }
        starImage.setAttribute("width", "20");
        starImage.setAttribute("height", "20");
        starImage.setAttribute("preserveAspectRatio", "xMidYMid slice");


        Node accNode = document.createElementNS(namespaceSVG, "text");
        Element acc = (Element) accNode;
        acc.setAttribute("class", "cls-44");
        if(type==0){
            acc.setAttribute("transform", "translate(115 306)");
        }
        else{
            acc.setAttribute("transform", "translate(57 250)");
        }
        acc.setTextContent(CommonTool.toString(scoreVO.getAccuracy() * 100).concat("%").concat(" // ").concat(String.valueOf(scoreVO.getMaxCombo())).concat("x"));


        Node ppValueNode = document.createElementNS(namespaceSVG, "text");
        Element ppValue = (Element) ppValueNode;
        if(type==0) {
            ppValue.setAttribute("transform", "translate(115 180)");
            ppValue.setAttribute("class", "cls-6");
            ppValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())).concat("pp"));
        }
        else if(type==2||type==3) {
            ppValue.setAttribute("transform", "translate(57 155)");
        }
        else {
            ppValue.setAttribute("transform", "translate(57 155)");
            ppValue.setAttribute("class", "cls-6");
            ppValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())).concat("pp"));
        }

        if(type==2||type==3)
        {
            Node originalPpValueNode = document.createElementNS(namespaceSVG, "tspan");
            Element originalPpValue = (Element) originalPpValueNode;
            originalPpValue.setAttribute("class", "cls-6");
            originalPpValue.setTextContent(String.valueOf(Math.round(scoreVO.getPpDetailsLocal().getCurrentPP())).concat("pp"));
            ppValue.appendChild(originalPpValueNode);
            if(Math.abs(scoreVO.getPpDetailsLocal().getCurrentPP()-scoreVO.getPp())>1.5)
            {
                Node arrowNode = document.createElementNS(namespaceSVG, "tspan");
                Element arrow = (Element) arrowNode;
                arrow.setAttribute("class", "cls-205");
                arrow.setTextContent("->");

                Node fixedPpValueNode = document.createElementNS(namespaceSVG, "tspan");
                Element fixedPpValue = (Element) fixedPpValueNode;
                fixedPpValue.setAttribute("class", "cls-206");
                fixedPpValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())).concat("pp"));
                ppValue.appendChild(arrowNode);
                ppValue.appendChild(fixedPpValueNode);
            }

        }


        Node indexNode = document.createElementNS(namespaceSVG, "text");
        Element indexElement = (Element) indexNode;
        indexElement.setAttribute("class", "cls-122");
        if(type==0) {
            indexElement.setAttribute("x", "365");
            indexElement.setAttribute("y", "385");
        }
        else {
            indexElement.setAttribute("x", "250");
            indexElement.setAttribute("y", "310");
        }
        indexElement.setAttribute("text-anchor", "middle");
        indexElement.setAttribute("clip-path", "url(#cardClip)");
        if(type==2||type==3||type==4)
        {
            indexElement.setTextContent("#".concat(String.valueOf(scoreVO.getPositionInList()+1)));
        }
        else {
            indexElement.setTextContent("#".concat(String.valueOf(index+offset)));
        }

        Node bidNode = document.createElementNS(namespaceSVG, "text");
        Element bid = (Element) bidNode;
        bid.setAttribute("class", "cls-4");
        if(type==0) {
            bid.setAttribute("transform", "translate(140 385)");
            bid.setAttribute("text-anchor", "middle");
        }
        else {
            bid.setAttribute("transform", "translate(57 309)");
        }
        bid.setAttribute("opacity", "0.4");
        bid.setTextContent(String.valueOf(scoreVO.getBeatmap().getBid()));


        Node acheveTimeNode = document.createElementNS(namespaceSVG, "text");
        Element acheveTime = (Element) acheveTimeNode;
        acheveTime.setAttribute("class", "cls-1");
        if(type==0) {
            acheveTime.setAttribute("transform", "translate(115 130)");
        }
        else {
            acheveTime.setAttribute("transform", "translate(57 110)");
        }
        acheveTime.setTextContent(convertDate(scoreVO.getCreate_at()));


        String artistAndMapper=scoreVO.getBeatmap().getArtist().concat(" // ").concat(scoreVO.getBeatmap().getCreator());
        if (artistAndMapper.length() >= 35) {
            artistAndMapper = artistAndMapper.substring(0, 34) + "...";
        }
        Node artistNode = document.createElementNS(namespaceSVG, "text");
        Element artist = (Element) artistNode;
        artist.setAttribute("class", "cls-4");
        if(type==0) {
            artist.setAttribute("transform", "translate(115 258)");
        }
        else {
            artist.setAttribute("transform", "translate(57 210)");
        }
        artist.setTextContent(artistAndMapper);


        Node linearGradientNode = document.createElementNS(namespaceSVG, "linearGradient");
        Element linearGradient = (Element) linearGradientNode;
        linearGradient.setAttributeNS(null, "id", "gradient-".concat(String.valueOf(index)));
        if(type==0)
        {
            linearGradient.setAttributeNS(null, "x1", "90");
            linearGradient.setAttributeNS(null, "y1", "405");
            linearGradient.setAttributeNS(null, "x2", "410");
            linearGradient.setAttributeNS(null, "y2", "405");
        }
        else {
            linearGradient.setAttributeNS(null, "x1", "40");
            linearGradient.setAttributeNS(null, "y1", "320");
            linearGradient.setAttributeNS(null, "x2", "280");
            linearGradient.setAttributeNS(null, "y2", "320");
        }
        linearGradient.setAttributeNS(null, "gradientUnits", "userSpaceOnUse");
        Element stop1 = document.createElementNS(namespaceSVG, "stop");
        stop1.setAttributeNS(null, "offset", "0");
        stop1.setAttributeNS(null, "stop-color", RankColor.fromString(scoreVO.getRank()).getDarkRankColorHEX());
        linearGradient.appendChild(stop1);
        Element stop2 = document.createElementNS(namespaceSVG, "stop");
        if(type==0){
            stop2.setAttributeNS(null, "offset", "0.85");
        }
        else {
            stop2.setAttributeNS(null, "offset", "0.77");
        }
        stop2.setAttributeNS(null, "stop-color", RankColor.fromString(scoreVO.getRank()).getDarkRankColorHEX());
        linearGradient.appendChild(stop2);
        Element stop3 = document.createElementNS(namespaceSVG, "stop");
        if(type==0){
            stop3.setAttributeNS(null, "offset", "0.9");
        }
        else {
            stop3.setAttributeNS(null, "offset", "0.87");
        }
        stop3.setAttributeNS(null, "stop-color", diffColor);
        linearGradient.appendChild(stop3);


        Node lineNode = document.createElementNS(namespaceSVG, "rect");
        Element line = (Element) lineNode;
        line.setAttribute("fill","url(#gradient-".concat(String.valueOf(index)).concat(")"));
        if(type==0)
        {
            line.setAttribute("x", "90");
            line.setAttribute("y", "400");
            line.setAttribute("width", "320");
        }
        else {
            line.setAttribute("x", "40");
            line.setAttribute("y", "320");
            line.setAttribute("width", "240");
        }
        line.setAttribute("height", "10");

        if(type==0)
        {
            if (total == 1) {
                sectionFull.setAttribute("transform", "translate(350,0)");
            }
            else if (total == 2) {
                sectionFull.setAttribute("transform", "translate(" + (175 + index * 350) + " 0)");
            }
            else {
                sectionFull.setAttribute("transform",
                        "translate(" + 350 * (index % 3) + " " + 350 * ((int) (index / 3)) + ")");
            }
        }
        else if(type==2||type==3||type==4) {
            if (total == 1) {
                sectionFull.setAttribute("transform", "translate(270,70)");
            }
            else if (total == 2) {
                sectionFull.setAttribute("transform", "translate(" + (135 + index * 270) + " 70)");
            }
            else {
                sectionFull.setAttribute("transform",
                        "translate(" + 270 * (index % 3) + " " + (270 * ((int) (index / 3)) +70) + ")");
            }
        }
        else {
            if (total == 1) {
                sectionFull.setAttribute("transform", "translate(270,0)");
            }
            else if (total == 2) {
                sectionFull.setAttribute("transform", "translate(" + 135 + index * 270 + " 0)");
            }
            else {
                sectionFull.setAttribute("transform",
                        "translate(" + 270 * (index % 3) + " " + 270 * ((int) (index / 3)) + ")");
            }
        }

        defs.appendChild(linearGradient);
        sectionFull.appendChild(listSubSection);
        sectionFull.appendChild(mapBG);
        sectionFull.appendChild(listSubSectionOpacity);
        sectionFull.appendChild(linearGradient);
        sectionFull.appendChild(line);
        sectionFull.appendChild(bid);
        sectionFull.appendChild(indexElement);
        sectionFull.appendChild(acheveTime);
        sectionFull.appendChild(artist);
        sectionFull.appendChild(songTitle);
        sectionFull.appendChild(difficulty);
        sectionFull.appendChild(star);
        sectionFull.appendChild(starImage);
        sectionFull.appendChild(acc);
        sectionFull.appendChild(ppValue);

        //去除Stable成绩的CL，但是我找不出其他方式显示是否是lazer成绩了所以就这样小溪

//        if(!scoreVO.getIsLazer()) {
//            scoreVO.setModJSON(scoreVO.getModJSON().stream()
//                    .filter(mod -> !mod.getAcronym().equals("CL"))
//                    .collect(Collectors.toList()));
//        }

        if (scoreVO.getModJSON().size()>0) {

            for(int j=0;j<scoreVO.getModJSON().size();j++)
            {
                appendBpCardModIcon(document, scoreVO.getModJSON().get(j), sectionFull, j,type);
            }
        }
        svgRoot.appendChild(sectionFull);
    }
    public static Document createInfoCard(PlayerInfoVO playerInfoVO)
    {
        try
        {
            // 构建输入 SVG 文件的 URI
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/infoSvgRenewal.svg");
            URI inputUri = filePath.toFile().toURI();
            int backgroundStyleIndicator=(int)(Math.random()*2);
            Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
            NodeList imageElements = doc.getElementsByTagName("image");
            for (int i = 0; i < imageElements.getLength(); i++) {
                Element imageElement = (Element) imageElements.item(i);
                String id = imageElement.getAttribute("id");
                switch (id)
                {
                    case "imageForAvatar":
                        imageElement.setAttributeNS(xlinkns,"xlink:href", playerInfoVO.getAvatarUrl());
                        break;
                    case "mainBG":
                        imageElement.setAttributeNS(xlinkns,"xlink:href", "assets/bg-".concat(String.valueOf(backgroundStyleIndicator).concat(".png")));
                        break;
                }
            }

            int desiredWidth;
            if("China".equals(playerInfoVO.getCountry()))
            {
                desiredWidth=185;
            }
            else
            {
                desiredWidth = ((CommonTool.textWidthRough(playerInfoVO.getCountry())) + 4) * 11 + 55;
            }
            Element rectBackgroundForCountry=doc.getElementById("baseLayerForHeader");
            rectBackgroundForCountry.setAttribute("width",String.valueOf(desiredWidth));
            Element bottomBackgroundBlock=doc.getElementById("bottomLayer");
            String modeColor="#5b7cca";
            switch (backgroundStyleIndicator)
            {
                case 0:
                    bottomBackgroundBlock.setAttribute("style","fill:#5b7cca");
                    rectBackgroundForCountry.setAttribute("style","fill:#f47079");
                    break;
                case 1:
                    bottomBackgroundBlock.setAttribute("style","fill:#fdc5d7");
                    rectBackgroundForCountry.setAttribute("style","fill:#989de1");
                    modeColor="#fdc5d7";
                    break;
            }
            NodeList textElements = doc.getElementsByTagName("text");
            for (int i = 0; i < textElements.getLength(); i++)
            {
                Element textElement = (Element) textElements.item(i);
                String id = textElement.getAttribute("id");
                switch (id)
                {
                    case "playerName":
                        textElement.setTextContent(playerInfoVO.getPlayerName());
                        break;
                    case "country":
                        textElement.setTextContent(playerInfoVO.getCountry());
                        break;
                    case "globalRankNumber":
                        textElement.setTextContent("#".concat(CommonTool.toString(playerInfoVO.getGlobalRank())));
                        break;
                    case "countryRankNumber":
                        textElement.setTextContent("#".concat(CommonTool.toString(playerInfoVO.getCountryRank())));
                        break;
                    case "PP":
                        textElement.setTextContent(CommonTool.toString(playerInfoVO.getPerformancePoint()));
                        break;
                    case "accuracy":
                        textElement.setTextContent(CommonTool.toString(playerInfoVO.getAccuracy()).concat("%"));
                        break;
                    case "Playtime":
                        textElement.setTextContent(CommonTool.formatSecondsToHours(playerInfoVO.getTotalPlayTime()).concat("h"));
                        break;
                    case "Playcount":
                        textElement.setTextContent(CommonTool.toString(playerInfoVO.getPlayCount()));
                        break;
                    case "tth":
                        textElement.setTextContent(CommonTool.transformNumber(playerInfoVO.getTotalHitCount().toString()));
                        break;
                    case "Hitsperplay":
                        textElement.setTextContent(CommonTool.toString(playerInfoVO.getTotalHitCount()/playerInfoVO.getPlayCount()));
                        break;
                    case "timeStamp":
                        textElement.setTextContent(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        break;
                }
            }
            doc.getElementById(OsuMode.getMode(playerInfoVO.getMode()).getDescribe()).setAttribute("fill",modeColor);
            return doc;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("Info卡片生成失败");
        }
    }
    private static void appendBpCardModIcon(Document document, Mod mod, Element sectionFull, int index, int panelVersion) {
        Node modSingleNode = document.createElementNS(namespaceSVG, "g");
        Element modSingle = (Element) modSingleNode;

        Node rectBGNode = document.createElementNS(namespaceSVG, "rect");
        Element rectBG  = (Element) rectBGNode ;
        if(panelVersion==0)
        {
            rectBG.setAttribute("transform", "translate(115 320)");
            rectBG.setAttribute("rx", "7.5");
            rectBG.setAttribute("ry", "7.5");
            rectBG.setAttribute("width", "30");
            rectBG.setAttribute("height", "15");
        }
        else {
            rectBG.setAttribute("transform", "translate(57 265)");
            rectBG.setAttribute("rx", "5.5");
            rectBG.setAttribute("ry", "5.5");
            rectBG.setAttribute("width", "22");
            rectBG.setAttribute("height", "11");
        }
        rectBG .setAttribute("fill", ModColor.getModTypeColorHEX(mod));

        Node modAcronymNode = document.createElementNS(namespaceSVG, "text");
        Element modAcronym = (Element) modAcronymNode;
        modAcronym.setAttribute("class", "cls-120");
        if(panelVersion==0) {
            modAcronym.setAttribute("transform", "translate(130 332)");
        }
        else {
            modAcronym.setAttribute("transform", "translate(68 273.5)");
        }
        modAcronym.setAttribute("text-anchor", "middle");
        modAcronym.setTextContent(mod.getAcronym());
        modSingle.appendChild(rectBG);
        modSingle.appendChild(modAcronym);
        if(panelVersion==0)
            modSingle.setAttribute("transform", "translate("+ 35*index +" 0)");
        else
            modSingle.setAttribute("transform", "translate("+ 27*index +" 0)");

        sectionFull.appendChild(modSingleNode);
    }






    public static String documentToString(Document doc) throws Exception {
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    public static InputStream documentToInputStream(Document doc) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(byteArrayOutputStream));
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public static OutputStream svgToPng(TranscoderInput input) {
        try {
            TranscoderOutput output = new TranscoderOutput(new ByteArrayOutputStream());
            transcoder.transcode(input, output);
            return output.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void svgToPng(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        try (OutputStream os = Files.newOutputStream(outFile.toPath())) {
            TranscoderOutput output = new TranscoderOutput(os);
            transcoder.transcode(input, output);
        }
    }

    public static void svgToPngForCompare(TranscoderInput input, File outFile) {
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 1200f);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 11440f);
        try (OutputStream os = Files.newOutputStream(outFile.toPath())) {
            TranscoderOutput output = new TranscoderOutput(os);
            transcoder.transcode(input, output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            transcoder.removeTranscodingHint(PNGTranscoder.KEY_WIDTH);
            transcoder.removeTranscodingHint(PNGTranscoder.KEY_HEIGHT);
        }
    }


    public static String convertDate(String inputDate) {
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM. d'th', yyyy", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
    public static Document createInfoPanel(PlayerInfoVO playerInfo,ProfileTheme theme) throws IOException
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/InfoV2-WhiteSpace.svg");
        URI inputUri = filePath.toFile().toURI();
        Document document = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
        Element svgRoot = document.getDocumentElement();
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        document.getElementById("playername").setTextContent(playerInfo.getPlayerName());
        document.getElementById("requestTime").setTextContent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        document.getElementById("countryAbbrv").setTextContent(playerInfo.getCountryCode());
        document.getElementById("countryRank").setTextContent(String.valueOf(playerInfo.getCountryRank()));
        document.getElementById("globalRank").setTextContent("#".concat(String.valueOf(playerInfo.getGlobalRank())));
        document.getElementById("ppValue").setTextContent(String.valueOf(Math.round(playerInfo.getPerformancePoint())));
        document.getElementById("rankedScore").setTextContent(formatter.format(playerInfo.getRankTotalScore()));
        document.getElementById("accuracy").setTextContent(CommonTool.toString(playerInfo.getAccuracy()).concat("%"));
        document.getElementById("playCount").setTextContent(formatter.format(playerInfo.getPlayCount()));
        document.getElementById("totalScore").setTextContent(formatter.format(playerInfo.getTotalScore()));
        document.getElementById("totalHits").setTextContent(formatter.format(playerInfo.getTotalHitCount()));
        document.getElementById("playTime").setTextContent(CommonTool.formatSecondsToHours(playerInfo.getTotalPlayTime()).concat("h"));
        document.getElementById("level").setTextContent(String.valueOf(playerInfo.getLevel()));
        document.getElementById("levelPercentage").setTextContent(String.valueOf(playerInfo.getLevelProgress()).concat("%"));
        document.getElementById("osu").setAttribute("fill", theme.getModeInactiveColor().toString());
        document.getElementById("taiko").setAttribute("fill", theme.getModeInactiveColor().toString());
        document.getElementById("mania").setAttribute("fill", theme.getModeInactiveColor().toString());
        document.getElementById("fruits").setAttribute("fill", theme.getModeInactiveColor().toString());

        document.getElementById(OsuMode.getMode(playerInfo.getMode()).getDescribe()).setAttribute("fill", theme.getMainColor().toString());
        document.getElementById("mode-underline").setAttribute("transform", "translate(" + 50*OsuMode.getMode(playerInfo.getMode()).getValue()  +" , 0)");
        Element imageElement = document.getElementById("avatar");
        if (playerInfo.getAvatarUrl()!= null) {
            imageElement.setAttributeNS(xlinkns, "xlink:href", playerInfo.getAvatarUrl());
        }
        document.getElementById("levelProgressRect").setAttribute("width",String.valueOf(8.5*playerInfo.getLevelProgress()));
        setupProfileRankGraph(document,playerInfo,theme);
        setupProfileBps(document,playerInfo,theme);
        profileColorTheme(document,theme);
        setupProfileBackground(document,playerInfo.getProfileBackgroundUrl(),true);
        return document;
    }
    private static void setupProfileRankGraph(Document document,PlayerInfoVO playerInfo,ProfileTheme theme)
    {
        Element rankGraphGroup=document.getElementById("rankGraphGroup");
        if(theme.getThemeType()== ProfileTheme.ThemeType.DARK) {
            document.getElementById("line1").setAttribute("stroke", "#fff");
            document.getElementById("line2").setAttribute("stroke", "#fff");
            document.getElementById("line3").setAttribute("stroke", "#fff");
        }
        int size = playerInfo.getRankHistory().size();
        List<Integer> rankHistory;
        try{
            rankHistory = playerInfo.getRankHistory().subList(size-8,size);
        }
        catch (Exception e) {
            logger.info("Profile rank history invalid, using default rank history");
            rankHistory = List.of(0,0,0,0,0,0,0,0);
        }
        int[] data = rankHistory.stream().mapToInt(Integer::intValue).toArray();

        int[] xPositions = {90, 130, 170, 210, 250, 290, 330, 370};
        int yMin = 350, yMax = 420;

        int dataMin = Integer.MAX_VALUE;
        int dataMax = Integer.MIN_VALUE;
        for (int value : data) {
            if (value < dataMin) dataMin = value;
            if (value > dataMax) dataMax = value;
        }
        StringBuilder polylinePoints = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int x = xPositions[i];
            int y;

            if (dataMax <= dataMin) y=385;
            else y = (int) ((data[i] - dataMin) / (double) (dataMax - dataMin) * (yMax - yMin) + yMin);
            polylinePoints.append(x).append(",").append(y).append(" ");

            Node circleNode = document.createElementNS(namespaceSVG, "circle");
            Element circle = (Element) circleNode;
            circle.setAttribute("cx", String.valueOf(x));
            circle.setAttribute("cy", String.valueOf(y));
            circle.setAttribute("r", "3.5");
            circle.setAttribute("fill", theme.getMainColor().toString());
            rankGraphGroup.appendChild(circle);
        }

        Node polylineNode = document.createElementNS(namespaceSVG, "polyline");
        Element polyline = (Element) polylineNode;
        polyline.setAttribute("points", polylinePoints.toString());
        polyline.setAttribute("fill", "none");
        polyline.setAttribute("stroke", theme.getMainColor().toString());
        polyline.setAttribute("stroke-width", "2");
        rankGraphGroup.appendChild(polyline);

        document.getElementById("rankGraph-label-1").setTextContent(CommonTool.abbrNumber(dataMin));
        document.getElementById("rankGraph-label-2").setTextContent(CommonTool.abbrNumber(dataMax));
    }
    public static Document createPPPlusPanel(PPPlusPerformance performance, PlayerInfoVO player) throws IOException
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/PPplusCard.svg");
        URI inputUri = filePath.toFile().toURI();
        Document document = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
        Element svgRoot = document.getDocumentElement();
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        boolean isWarmColor = CommonTool.isWarmColor(player.getPrimaryColor());
        HSL mainColor = new HSL(player.getPrimaryColor(), 100, 64);
        if (player.getPrimaryColor()>=172 && player.getPrimaryColor()<=195) mainColor= new HSL(player.getPrimaryColor(), 72, 50);
        HSL alternativeColor;

        document.getElementById("playername").setTextContent(player.getPlayerName());
        if(player.getPrimaryColor()>=234 && player.getPrimaryColor()<=292) {
            document.getElementById("playername").setAttribute("fill", "#e3e3e3");
        }
        document.getElementById("global-label").setAttribute("fill",mainColor.toString());
        document.getElementById("country-label").setAttribute("fill",mainColor.toString());
        document.getElementById("background").setAttribute("fill",mainColor.toString());
        if (player.getGlobalRank()!=null && player.getCountryRank()!=null)
        {
            document.getElementById("global-rank").setTextContent("#" + formatter.format(player.getGlobalRank()));
            document.getElementById("country-rank").setTextContent("#" + formatter.format(player.getCountryRank()));
        }
        else{
            document.getElementById("global-rank").setTextContent("#0");
            document.getElementById("country-rank").setTextContent("#0");
            document.getElementById("global-rank").setAttribute("opacity","0.7");
            document.getElementById("country-rank").setAttribute("opacity","0.7");
        }

        document.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", player.getAvatarUrl());

        int jumpAim= (int) Math.round(performance.getPpJumpAim());
        int flowAim= (int) Math.round(performance.getPpFlowAim());
        int speed= (int) Math.round(performance.getPpSpeed());
        int stamina= (int) Math.round(performance.getPpStamina());
        int precision= (int) Math.round(performance.getPpPrecision());
        int accuracy= (int) Math.round(performance.getPpAcc());
        int average= (int) Math.round((jumpAim+flowAim+speed+stamina+precision+accuracy)/6);

        document.getElementById("jump").setTextContent(String.valueOf(jumpAim));
        document.getElementById("flow").setTextContent(String.valueOf(flowAim));
        document.getElementById("speed").setTextContent(String.valueOf(speed));
        document.getElementById("stamina").setTextContent(String.valueOf(stamina));
        document.getElementById("precision").setTextContent(String.valueOf(precision));
        document.getElementById("accuracy").setTextContent(String.valueOf(accuracy));
        document.getElementById("average").setTextContent(String.valueOf(average));
        document.getElementById("total").setTextContent(String.valueOf(Math.round(performance.getPp())));


        if (isWarmColor) alternativeColor=new HSL(0, 0, 90);
        else alternativeColor = new HSL((CommonTool.circularHueSubtract(player.getPrimaryColor(),120)), 86, 52);
        document.getElementById("jump").setAttribute("fill", alternativeColor.toString());
        document.getElementById("flow").setAttribute("fill", alternativeColor.toString());
        document.getElementById("speed").setAttribute("fill", alternativeColor.toString());
        document.getElementById("stamina").setAttribute("fill", alternativeColor.toString());
        document.getElementById("precision").setAttribute("fill", alternativeColor.toString());
        document.getElementById("accuracy").setAttribute("fill", alternativeColor.toString());
        document.getElementById("average").setAttribute("fill", alternativeColor.toString());
        document.getElementById("total").setAttribute("fill", alternativeColor.toString());
        if (isWarmColor) alternativeColor=new HSL((CommonTool.circularHueSubtract(player.getPrimaryColor(),120)), 69, 35);

        document.getElementById("jump-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(jumpAim,
                        PerformanceDimensionLimit.JUMP.getLimitExpertPlus(),
                        PerformanceDimensionLimit.JUMP.getScaleFactor())));
        document.getElementById("jump-bar").setAttribute("fill", alternativeColor.toString());

        document.getElementById("flow-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(flowAim,
                        PerformanceDimensionLimit.FLOW.getLimitExpertPlus(),
                        PerformanceDimensionLimit.FLOW.getScaleFactor())));
        document.getElementById("flow-bar").setAttribute("fill", alternativeColor.toString());

        document.getElementById("speed-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(speed,
                        PerformanceDimensionLimit.SPEED.getLimitExpertPlus(),
                        PerformanceDimensionLimit.SPEED.getScaleFactor())));
        document.getElementById("speed-bar").setAttribute("fill", alternativeColor.toString());

        document.getElementById("stamina-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(stamina,
                        PerformanceDimensionLimit.STAMINA.getLimitExpertPlus(),
                        PerformanceDimensionLimit.STAMINA.getScaleFactor())));
        document.getElementById("stamina-bar").setAttribute("fill", alternativeColor.toString());

        document.getElementById("precision-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(precision,
                        PerformanceDimensionLimit.PRECISION.getLimitExpertPlus(),
                        PerformanceDimensionLimit.PRECISION.getScaleFactor())));
        document.getElementById("precision-bar").setAttribute("fill", alternativeColor.toString());

        document.getElementById("accuracy-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(accuracy,
                        PerformanceDimensionLimit.ACCURACY.getLimitExpertPlus(),
                        PerformanceDimensionLimit.ACCURACY.getScaleFactor())));
        document.getElementById("accuracy-bar").setAttribute("fill", alternativeColor.toString());

        document.getElementById("average-bar").setAttribute("width", CommonTool.toString(530*
                CommonTool.getScaledRatio(average,
                        PerformanceDimensionLimit.AVERAGE.getLimitExpertPlus(),
                        PerformanceDimensionLimit.AVERAGE.getScaleFactor())));
        document.getElementById("average-bar").setAttribute("fill", alternativeColor.toString());

        List<String> playStyleElements =Arrays.asList(
                "input-muse", "input-keyboard", "aim-tablet", "aim-mouse", "aim-touch"
        );
        if (player.getPlayStyles()!=null && !player.getPlayStyles().isEmpty())
        {
            for (String type : player.getPlayStyles()) {
                for (String element : playStyleElements) {
                    if (element.contains(type.toLowerCase().trim())) {
                        document.getElementById(element).setAttribute("fill", mainColor.toString());
                    }
                }
            }
            if (player.getPlayStyles().size()==1 && player.getPlayStyles().get(0).equalsIgnoreCase("mouse"))
            {
                document.getElementById("input-muse").setAttribute("fill", mainColor.toString());
            }

        }

        setupPPPlusTags(blessPlayerWithTags(performance,average),document,svgRoot,player.getPrimaryColor()-133);
        return document;
    }
    private static void setupPPPlusTags(List<PerformancePlusTag> tags, Document doc, Element sectionFull, Integer startHue) {
        if (tags.isEmpty()) return;
        int offset=0;
        int lastElementSize=0;
        startHue= (startHue+360) % 360;

        for(PerformancePlusTag tag:tags)
        {
            HSL textColor=new HSL(startHue,100,7);
            if(startHue>=233 && startHue<=292) {
                textColor=new HSL(startHue,0,90);
            }
            Node tagSingleNode = doc.createElementNS(namespaceSVG, "g");
            Element tagSingle = (Element) tagSingleNode;

            Node rectBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element rectBG = (Element) rectBGNode;
            rectBG.setAttribute("x", "15");
            rectBG.setAttribute("y", "70");
            rectBG.setAttribute("width", String.valueOf(tag.getElementSize()));
            rectBG.setAttribute("height", "20");
            rectBG.setAttribute("fill", new HSL(startHue, 87, 53).toString());

            Node tagNamemNode = doc.createElementNS(namespaceSVG, "text");
            Element tagName = (Element) tagNamemNode;
            tagName.setAttribute("class", "cls-2");
            tagName.setAttribute("x", String.valueOf(tag.getAnchor()));
            tagName.setAttribute("y", "100");
            tagName.setAttribute("font-size", "15px");
            tagName.setAttribute("transform", "scale(1 0.85)");
            tagName.setAttribute("font-weight", "700");
            tagName.setAttribute("text-anchor", "middle");
            tagName.setAttribute("fill", textColor.toString());
            tagName.setTextContent(tag.getName());

            tagSingle.appendChild(rectBG);
            tagSingle.appendChild(tagName);
            tagSingle.setAttribute("transform", "translate(" + offset+lastElementSize  + " 0)");
            sectionFull.appendChild(tagSingleNode);
            startHue+=35;
            lastElementSize+=tag.getElementSize()+10;
        }
    }
    private static List<PerformancePlusTag> blessPlayerWithTags(PPPlusPerformance performance, double averagePp)
    {
        Map<PerformanceDimensionLimit, Double> ppMap = Map.of(
                PerformanceDimensionLimit.JUMP, performance.getPpJumpAim(),
                PerformanceDimensionLimit.FLOW, performance.getPpFlowAim(),
                PerformanceDimensionLimit.SPEED, performance.getPpSpeed(),
                PerformanceDimensionLimit.STAMINA, performance.getPpStamina(),
                PerformanceDimensionLimit.PRECISION, performance.getPpPrecision(),
                PerformanceDimensionLimit.ACCURACY, performance.getPpAcc()
        );


        PerformanceDimensionLimit avgLimit = PerformanceDimensionLimit.AVERAGE;
        double avgScaled = CommonTool.getScaledRatio(averagePp, avgLimit.getLimitExpertPlus(), avgLimit.getScaleFactor());

        List<PerformancePlusTag> tags = new ArrayList<>();
        List<PerformancePlusTag> scaledMainTags = new ArrayList<>();
        long strongCount = 0;
        for (Map.Entry<PerformanceDimensionLimit, Double> entry : ppMap.entrySet()) {
            PerformanceDimensionLimit dim = entry.getKey();
            double value = entry.getValue();
            double scaled = CommonTool.getScaledRatio(value, dim.getLimitExpertPlus(), dim.getScaleFactor());
            if(scaled>=0.95) strongCount++;
            if (scaled >= avgScaled) {
                PerformancePlusTag tag = mapToTag(dim);
                if (tag != null) tags.add(tag);
            }
            if (scaled> (avgScaled*0.87)) scaledMainTags.add(mapToTag(dim));

        }
        if (strongCount>=4) {
            return List.of(PerformancePlusTag.OMNIPOTENT);
        }


        Set<PerformancePlusTag> tagSet = new HashSet<>(scaledMainTags);
        boolean hasAccuracy = tagSet.contains(PerformancePlusTag.ACCURATE);
        boolean hasAim = tagSet.contains(PerformancePlusTag.AIM);
        boolean hasFlow = tagSet.contains(PerformancePlusTag.FLOW);
        boolean hasSpeedyOrEnduring = tagSet.contains(PerformancePlusTag.SPEEDY) || tagSet.contains(PerformancePlusTag.ENDURING);

        if (hasAccuracy && hasAim && hasFlow && hasSpeedyOrEnduring) {
            return List.of(PerformancePlusTag.COMPREHENSIVE);
        }

        return tags.stream().limit(4).sorted().toList();
    }
    private static PerformancePlusTag mapToTag(PerformanceDimensionLimit dim)
    {
        return switch (dim) {
            case JUMP -> PerformancePlusTag.AIM;
            case FLOW -> PerformancePlusTag.FLOW;
            case SPEED -> PerformancePlusTag.SPEEDY;
            case STAMINA -> PerformancePlusTag.ENDURING;
            case PRECISION -> PerformancePlusTag.PRECISE;
            case ACCURACY -> PerformancePlusTag.ACCURATE;
            default -> null;
        };
    }






    private static void setupProfileBps(Document doc,PlayerInfoVO playerInfo,ProfileTheme theme)
    {
        int listIndex=0;
        List<ScoreVO> scoreList = playerInfo.getBps();
        for (ScoreVO score : scoreList)
        {
            Node sectionFullNode = doc.createElementNS(namespaceSVG, "g");
            Element sectionFull = (Element) sectionFullNode;

            Node totalBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element totalBG = (Element) totalBGNode;
            totalBG.setAttribute("rx", "15");
            totalBG.setAttribute("ry", "15");
            totalBG.setAttribute("width", "415");
            totalBG.setAttribute("height", "100");
            totalBG.setAttribute("fill", "#000");
            if(theme.getThemeType()== ProfileTheme.ThemeType.DARK)
                totalBG.setAttribute("fill", "#2a2933");
            totalBG.setAttribute("opacity", "0.1");
            totalBG.setAttribute("x", "25");
            totalBG.setAttribute("y", "580");

            Node mapBGImageNode = doc.createElementNS(namespaceSVG, "image");
            Element mapBGImage = (Element) mapBGImageNode;
            mapBGImage.setAttributeNS(xlinkns, "xlink:href", score.getBeatmap().getBgUrl());
            mapBGImage.setAttribute("x", "25");
            mapBGImage.setAttribute("y", "580");
            mapBGImage.setAttribute("width", "415");
            mapBGImage.setAttribute("opacity", "0.9");
            if(theme.getThemeType()== ProfileTheme.ThemeType.DARK)
                mapBGImage.setAttribute("opacity", "0.5");
            else
                mapBGImage.setAttribute("filter", "url(#bp-blur)");
            mapBGImage.setAttribute("height", "100");
            mapBGImage.setAttribute("clip-path", "url(#bpclip)");
            mapBGImage.setAttribute("preserveAspectRatio", "xMidYMid slice");

            Node totalBGMaskNode = doc.createElementNS(namespaceSVG, "rect");
            Element totalBGMask = (Element) totalBGMaskNode;
            totalBGMask.setAttribute("width", "415");
            totalBGMask.setAttribute("height", "100");
            if(theme.getThemeType()== ProfileTheme.ThemeType.DARK) {
                totalBGMask.setAttribute("fill", "#2a2933");
                totalBGMask.setAttribute("opacity", "0.5");
            }
            else
                totalBGMask.setAttribute("fill", "url(#opacityGraditent)");
            totalBGMask.setAttribute("clip-path", "url(#bpclip)");
            totalBGMask.setAttribute("x", "25");
            totalBGMask.setAttribute("y", "580");


            Node starAndSongTitleNode = doc.createElementNS(namespaceSVG, "text");
            Element starAndSongTitle = (Element) starAndSongTitleNode;
            starAndSongTitle.setAttribute("class", "cls-130");
            starAndSongTitle.setAttribute("fill", "#f1f4f3");
            starAndSongTitle.setAttribute("font-size", "15px");
            starAndSongTitle.setAttribute("font-weight", "600");
            starAndSongTitle.setAttribute("transform", "translate(40 623)");

            Node starNode = doc.createElementNS(namespaceSVG, "tspan");
            Element star = (Element) starNode;
            if(theme.getThemeType()== ProfileTheme.ThemeType.DARK) {
                star.setAttribute("fill", new HSL(theme.getHue(), 80, 85).toString());
            }
            else{
                star.setAttribute("fill", theme.getMainMiddleColor().toString());
            }
            star.setTextContent(CommonTool.toString(score.getBeatmap().getDifficult_rating()).concat("*"));

            Node divisorNode = doc.createElementNS(namespaceSVG, "tspan");
            Element divisor = (Element) divisorNode;
            divisor.setTextContent(" | ");

            Node titleNode = doc.createElementNS(namespaceSVG, "tspan");
            Element title = (Element) titleNode;
            String titleStr=score.getBeatmap().getTitle();
            if (titleStr.length() > 30) titleStr=titleStr.substring(0, 29).concat("...");
            title.setTextContent(titleStr);

            starAndSongTitle.appendChild(star);
            starAndSongTitle.appendChild(divisor);
            starAndSongTitle.appendChild(title);

            Node starAndSongTitleShadowNode = doc.createElementNS(namespaceSVG, "text");
            Element starAndSongShadowTitle = (Element) starAndSongTitleShadowNode;
            starAndSongShadowTitle.setAttribute("class", "cls-130");
            starAndSongShadowTitle.setAttribute("fill", "#000000");
            starAndSongShadowTitle.setAttribute("font-size", "15px");
            starAndSongShadowTitle.setAttribute("font-weight", "600");
            starAndSongShadowTitle.setAttribute("opacity", "0.4");
            starAndSongShadowTitle.setAttribute("transform", "translate(41.5 624.5)");

            Node starShadowNode = doc.createElementNS(namespaceSVG, "tspan");
            Element starShadow = (Element) starShadowNode;
            starShadow.setTextContent(CommonTool.toString(score.getBeatmap().getDifficult_rating()).concat("*"));

            Node divisorShadowNode = doc.createElementNS(namespaceSVG, "tspan");
            Element divisorShadow = (Element) divisorShadowNode;
            divisorShadow.setTextContent(" | ");

            Node titleShadowNode = doc.createElementNS(namespaceSVG, "tspan");
            Element titleShadow = (Element) titleShadowNode;
            titleShadow.setTextContent(titleStr);

            starAndSongShadowTitle.appendChild(starShadow);
            starAndSongShadowTitle.appendChild(divisorShadow);
            starAndSongShadowTitle.appendChild(titleShadow);


            Node bpmAndMapperNode = doc.createElementNS(namespaceSVG, "text");
            Element bpmAndMapper = (Element) bpmAndMapperNode;
            bpmAndMapper.setAttribute("class", "cls-130");
            bpmAndMapper.setAttribute("fill", "#f1f4f3");
            bpmAndMapper.setAttribute("font-size", "15px");
            bpmAndMapper.setAttribute("font-weight", "600");
            bpmAndMapper.setAttribute("transform", "translate(40 650)");

            Node bpmNode = doc.createElementNS(namespaceSVG, "tspan");
            Element bpm = (Element) bpmNode;
            if(theme.getThemeType()== ProfileTheme.ThemeType.DARK) {
                bpm.setAttribute("fill", new HSL(theme.getHue(), 80, 85).toString());
            }
            else {
                bpm.setAttribute("fill", theme.getMainMiddleColor().toString());
            }
            bpm.setTextContent(String.valueOf(Math.round(score.getBeatmap().getBpm())).concat(" BPM"));

            Node divisorNode2 = doc.createElementNS(namespaceSVG, "tspan");
            Element divisor2 = (Element) divisorNode2;
            divisor2.setTextContent(" | ");

            Node mapperNode = doc.createElementNS(namespaceSVG, "tspan");
            Element mapper = (Element) mapperNode;
            String mapperDiffStr=score.getBeatmap().getCreator().concat(" // [").concat(score.getBeatmap().getVersion()).concat("]");
            if (mapperDiffStr.length() > 29) mapperDiffStr=mapperDiffStr.substring(0, 28).concat("...");
            mapper.setTextContent(mapperDiffStr);

            bpmAndMapper.appendChild(bpm);
            bpmAndMapper.appendChild(divisor2);
            bpmAndMapper.appendChild(mapper);


            Node bpmAndMapperShadowNode = doc.createElementNS(namespaceSVG, "text");
            Element bpmAndMapperShadow = (Element) bpmAndMapperShadowNode;
            bpmAndMapperShadow.setAttribute("class", "cls-130");
            bpmAndMapperShadow.setAttribute("fill", "#000000");
            bpmAndMapperShadow.setAttribute("opacity", "0.4");
            bpmAndMapperShadow.setAttribute("font-size", "15px");
            bpmAndMapperShadow.setAttribute("font-weight", "600");
            bpmAndMapperShadow.setAttribute("transform", "translate(41.5 651.5)");

            Node bpmShadowNode = doc.createElementNS(namespaceSVG, "tspan");
            Element bpmShadow= (Element) bpmShadowNode;
            bpmShadow.setTextContent(String.valueOf(Math.round(score.getBeatmap().getBpm())).concat(" BPM"));

            Node divisorShadowNode2 = doc.createElementNS(namespaceSVG, "tspan");
            Element divisor2Shadow = (Element) divisorShadowNode2;
            divisor2Shadow.setTextContent(" | ");

            Node mapperShadowNode = doc.createElementNS(namespaceSVG, "tspan");
            Element mapperShadow = (Element) mapperShadowNode;
            mapperShadow.setTextContent(mapperDiffStr);

            bpmAndMapperShadow.appendChild(bpmShadow);
            bpmAndMapperShadow.appendChild(divisor2Shadow);
            bpmAndMapperShadow.appendChild(mapperShadow);



            Node ppNode = doc.createElementNS(namespaceSVG, "text");
            Element pp = (Element) ppNode;
            pp.setAttribute("class", "cls-130");
            pp.setAttribute("font-size", "28px");
            pp.setAttribute("fill", "#f269a1");
            pp.setAttribute("font-weight", "600");
            pp.setAttribute("x", "435");
            pp.setAttribute("text-anchor", "end");
            pp.setAttribute("y", "640");
            pp.setTextContent(String.valueOf(Math.round(score.getPp())).concat("pp"));

            Node iffcNode = doc.createElementNS(namespaceSVG, "text");
            Element iffc = (Element) iffcNode;
            iffc.setAttribute("class", "cls-130");
            iffc.setAttribute("font-size", "12px");
            iffc.setAttribute("font-weight", "600");
            iffc.setAttribute("x", "430");
            iffc.setAttribute("y", "658");
            iffc.setAttribute("text-anchor", "end");

            Node iffcLabelNode = doc.createElementNS(namespaceSVG, "tspan");
            Element iffcLabel = (Element) iffcLabelNode;
            if(theme.getThemeType()== ProfileTheme.ThemeType.LIGHT)
                iffcLabel.setAttribute("fill", "#333333");
            else
                iffcLabel.setAttribute("fill", "#f3f3f3");
            iffcLabel.setTextContent("if fc ");

            Node iffcNumberNode = doc.createElementNS(namespaceSVG, "tspan");
            Element iffcNumber = (Element) iffcNumberNode;
            iffcNumber.setTextContent(String.valueOf(Math.round(score.getPpDetailsLocal().getIfFc())).concat("pp"));
            iffcNumber.setAttribute("fill", "#f269a1");
            iffc.appendChild(iffcLabel);
            iffc.appendChild(iffcNumber);

            sectionFull.appendChild(mapBGImage);
            sectionFull.appendChild(totalBG);
            sectionFull.appendChild(totalBGMask);

            if(theme.getThemeType()== ProfileTheme.ThemeType.LIGHT)
            {
                Node rankNode = doc.createElementNS(namespaceSVG, "text");
                Element rank = (Element) rankNode;
                rank.setAttribute("class", "cls-130");
                rank.setAttribute("font-size", "100px");
                rank.setAttribute("fill", RankColor.fromString(score.getRank()).getDarkRankColorHEX());
                rank.setAttribute("clip-path", "url(#bpclip)");
                rank.setAttribute("opacity", "0.5");
                rank.setAttribute("font-weight", "600");
                rank.setAttribute("x", "384");
                rank.setAttribute("y", "685");
                rank.setTextContent(score.getRank());
                sectionFull.appendChild(rank);
            }
            else {
                Node rankNode = doc.createElementNS(namespaceSVG, "rect");
                Element rank = (Element) rankNode;
                rank.setAttribute("width", "35");
                rank.setAttribute("height", "3");
                rank.setAttribute("fill", RankColor.fromString(score.getRank()).getDarkRankColorHEX());
                rank.setAttribute("transform", "translate(50,579.5)");
                rank.setAttribute("rx", "1.5");
                rank.setAttribute("ry", "1.5");
                rank.setTextContent(score.getRank());
                sectionFull.appendChild(rank);
            }
            sectionFull.appendChild(starAndSongShadowTitle);
            sectionFull.appendChild(bpmAndMapperShadow);
            sectionFull.appendChild(starAndSongTitle);
            sectionFull.appendChild(bpmAndMapper);
            sectionFull.appendChild(pp);
            sectionFull.appendChild(iffc);

            setupModIconForProfileBps(score.getModJSON(), doc, sectionFull);
            sectionFull.setAttribute("opacity","0.9");
            sectionFull.setAttribute("transform", "translate("+ 435*(listIndex%2)+ "," + 120 * (int)(listIndex/2) + ")");
            doc.getElementById("bp-block-all").appendChild(sectionFull);
            listIndex++;
        }
    }
    private static void setupModIconForProfileBps(List<Mod> modList, Document doc, Element sectionFull) {
        if (modList.isEmpty()) return;
        modList=modList.reversed();
        for(int i=0;i<modList.size();i++)
        {
            Node modSingleNode = doc.createElementNS(namespaceSVG, "g");
            Element modSingle = (Element) modSingleNode;
            Node rectBGNode = doc.createElementNS(namespaceSVG, "rect");
            Element rectBG = (Element) rectBGNode;
            rectBG.setAttribute("transform", "translate(408.5 595.5)");
            rectBG.setAttribute("rx", "6");
            rectBG.setAttribute("ry", "6");
            rectBG.setAttribute("width", "22");
            rectBG.setAttribute("height", "12");
            rectBG.setAttribute("fill", ModColor.getModTypeColorHEX(modList.get(i)));

            Node modAcronymNode = doc.createElementNS(namespaceSVG, "text");
            Element modAcronym = (Element) modAcronymNode;
            modAcronym.setAttribute("class", "cls-112");
            modAcronym.setAttribute("transform", "translate(420 605)");
            modAcronym.setAttribute("text-anchor", "middle");
            modAcronym.setTextContent(modList.get(i).getAcronym());

            modSingle.appendChild(rectBG);
            modSingle.appendChild(modAcronym);
            modSingle.setAttribute("transform", "translate(" + -25*i  + " 0)");
            sectionFull.appendChild(modSingleNode);
        }
    }


    private static void setupProfileBackground(Document doc,String filename,Boolean enableGlassEffect)
    {
        doc.getElementById("bg-0").setAttributeNS(xlinkns,"xlink:href", filename);
        doc.getElementById("bg-1").setAttributeNS(xlinkns,"xlink:href", filename);
        doc.getElementById("bg-2").setAttributeNS(xlinkns,"xlink:href", filename);
        doc.getElementById("bg-3").setAttributeNS(xlinkns,"xlink:href", filename);
        if (!enableGlassEffect) {
            doc.getElementById("bg-1").setAttribute("opacity", "0");
            doc.getElementById("bg-2").setAttribute("opacity", "0");
            doc.getElementById("bg-3").setAttribute("opacity", "0");
        }
    }
    private static void profileColorTheme(Document doc, ProfileTheme theme)
    {
        doc.getElementById("headerBorder").setAttribute("fill", theme.getHeaderBorderColor().toString());
        doc.getElementById("header").setAttribute("fill", theme.getHeaderColor().toString());
        doc.getElementById("avatar-block").setAttribute("fill", theme.getBlockColor().toString());
        doc.getElementById("avatar-block").setAttribute("stroke", theme.getBorderColor().toString());
        doc.getElementById("status-block").setAttribute("fill", theme.getBlockColorLighter().toString());
        doc.getElementById("status-block").setAttribute("stroke", theme.getBlockColor().toString());
        doc.getElementById("bp-block").setAttribute("fill", theme.getBlockColor().toString());
        doc.getElementById("bp-block").setAttribute("stroke", theme.getBorderColor().toString());
        doc.getElementById("rankGraphBG").setAttribute("fill", theme.getBlockColor().toString());
        doc.getElementById("mode-underline").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("levelProgressBG").setAttribute("fill", theme.getLevelProgressBackgroundColor().toString());

        doc.getElementById("playername").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("requestTime").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("requestTimeLabel").setAttribute("fill", theme.getMainMiddleColor().toString());
        doc.getElementById("levelProgressRect").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("levelPercentage").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("level").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("contryBorder").setAttribute("stroke", theme.getMainColor().toString());
        doc.getElementById("countryAbbrv").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("globalRank").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("globalLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("rankedScoreLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("rankedScore").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("accuracyLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("accuracy").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("playCountLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("playCount").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("totalScoreLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("totalScore").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("totalHitsLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("totalHits").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("playTimeLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("playTime").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("rankGraph-label-1").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("rankGraph-label-2").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("bpLabel").setAttribute("fill", theme.getMainColor().toString());
        doc.getElementById("countryRankAll").setAttribute("fill", theme.getMainColor().toString());






    }
    private static void textElementBackgournd(Document doc, String elementId, int padding, int borderRadius, String color)
    {
        UserAgent userAgent = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(userAgent);
        BridgeContext ctx = new BridgeContext(userAgent, loader);
        GVTBuilder builder = new GVTBuilder();
        GraphicsNode rootNode = builder.build(ctx, doc);

        Element textElement = doc.getElementById(elementId);
        GraphicsNode textNode = ctx.getGraphicsNode(textElement);

        if (textNode != null) {
            Rectangle2D bounds = textNode.getBounds();
            logger.trace("Text bounds: " + bounds);
            Element rect = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
            rect.setAttribute("x", String.valueOf(bounds.getX() - padding));
            rect.setAttribute("y", String.valueOf(bounds.getY() - padding));
            rect.setAttribute("width", String.valueOf(bounds.getWidth() + padding + padding));
            rect.setAttribute("height", String.valueOf(bounds.getHeight() + padding + padding));
            rect.setAttribute("fill", color);
            rect.setAttribute("rx", String.valueOf(borderRadius));
            rect.setAttribute("ry", String.valueOf(borderRadius));

            Node parent = textElement.getParentNode();
            parent.insertBefore(rect, textElement);

        } else {
            logger.warn(elementId + ": 无法获取文本节点");
        }
    }
    public static String accuracyColorMid(double accuracy) {
        accuracy = Math.max(0.0, Math.min(1.0, accuracy));
        int h, s = 96, l;
        if (accuracy >= 1.0) {
            h = 102;
            l = 80;
        } else if (accuracy >= 0.9) {
            double t = (accuracy - 0.9) / 0.1;
            h = (int) Math.round(70 + (102 - 70) * t);
            l = (int) Math.round(75 + (80 - 75) * t);
        } else if (accuracy >= 0.8) {
            double t = (accuracy - 0.8) / 0.1;
            h = (int) Math.round(23 + (70 - 23) * t);
            l = (int) Math.round(70 + (75 - 70) * t);
        } else {
            h = 23;
            l = 70;
        }
        return String.format("hsl(%d,%d%%,%d%%)", h, s, l);
    }

}
