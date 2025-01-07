package me.aloic.lazybot.osu.utils;

import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.util.CommonTool;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
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
            throw new RuntimeException(e);
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

    public static OutputStream scoreToImageDarkApache(ScoreVO targetScore) throws TranscoderException, IOException
    {
        Document doc = getScorePanelDarkModeDoc(targetScore);
        long startingTime = System.currentTimeMillis();
        TranscoderInput input = new TranscoderInput(doc);
        OutputStream o =svgToPng(input);
        logger.info("Render cost(dark mode):" + (System.currentTimeMillis() - startingTime) + "ms");
        return o;
    }

    public static String scoreToImageDarkURLApache(ScoreVO targetScore) throws TranscoderException, IOException
    {
        Document doc = getScorePanelDarkModeDoc(targetScore);
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
        else
        {
            throw new RuntimeException("error when wiring mod icon");
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
            appendModIcon(document, scoreVO, sectionFull, 0);
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

        if (scoreVO.getMods() != null)
        {
            appendModIcon(document, scoreVO, sectionFull, 1);
        }
        if (type == 0)
        {
            sectionFull.setAttribute("transform", "translate(0 ".concat(String.valueOf(index * 85)).concat(")"));
        }
        else
        {
            sectionFull.setAttribute("transform", "translate(870 ".concat(String.valueOf(index * 85)).concat(")"));
        }
        svgRoot.appendChild(sectionFull);
    }

    private static void appendModIcon(Document document, ScoreVO scoreVO, Element sectionFull, int type)
    {
        for (int i = 0; i < scoreVO.getMods().length; i++)
        {
            switch (scoreVO.getMods()[i])
            {
                case "HR":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "HR", "#fb0038", type));
                    break;
                }
                case "HD":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "HD", "#fff869", type));
                    break;
                }
                case "DT":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "DT", "#7251f7", type));
                    break;
                }
                case "NF":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "NF", "#33b5f1", type));
                    break;
                }
                case "SO":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "SO", "#cd6cbf", type));
                    break;
                }
                case "FL":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "FL", "303030", type));
                    break;
                }
                case "SD":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "SD", "#926e4c", type));
                    break;
                }
                case "EZ":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "EZ", "#38a772", type));
                    break;
                }
                case "NC":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "NC", "#d625ff", type));
                    break;
                }
                case "PF":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "PF", "#e5a565", type));
                    break;
                }
                case "HT":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "HT", "#555a5d", type));
                    break;
                }
                case "TD":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "TD", "#4dbcee", type));
                    break;
                }
                case "RX":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "RX", "#4dbcee", type));
                    break;
                }
                case "AP":
                {
                    sectionFull.appendChild(wireModIconForList(document, i, "AP", "#4dbcee", type));
                    break;
                }
            }
        }
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
            doc.getElementById("mode").setTextContent(targetScore.getMode().concat("!"));
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
            if (targetScore.getBeatmap().getTitle().length() < 24)
            {
                doc.getElementById("songTitle1").setTextContent(targetScore.getBeatmap().getTitle());
            }
            else
            {
                doc.getElementById("songTitle1").setTextContent(targetScore.getBeatmap().getTitle().substring(0, 23).concat("..."));
            }
            doc.getElementById(targetScore.getBeatmap().getStatus() + "Status").setAttribute("opacity", "1");
            doc.getElementById(targetScore.getBeatmap().getStatus() + "BG").setAttribute("opacity", "1");
            Element grade = doc.getElementById("grade");
            doc.getElementById("gradeShadow").setTextContent(targetScore.getRank());
            grade.setTextContent(targetScore.getRank());
            grade.setAttribute("fill", rankColorIndictor(targetScore.getRank()));
            logger.info("Modify time (white Mode):" + (System.currentTimeMillis() - startingTime) + "ms");
            return doc;
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Document getScorePanelDarkModeDoc(ScoreVO targetScore)
    {
        long startingTime = System.currentTimeMillis();
        try
        {
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/scorePanelDarkmode.svg");
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
                            if (targetScore.getAvatarUrl().startsWith("http"))
                            {
                                logger.info("Using HTTP Asserts: Avatar Link");
                                if (!CommonTool.isCorruptedLink(targetScore.getAvatarUrl()))
                                {
                                    imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getAvatarUrl());
                                }
                            }
                            else
                            {
                                imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getAvatarUrl());
                            }
                        }
                        break;
                    case "mapBg-right":
                        if (targetScore.getBeatmap().getBgUrl() != null)
                        {
                            if (targetScore.getBeatmap().getBgUrl().startsWith("http"))
                            {
                                logger.info("Using HTTP Asserts: Beatmap background Link");
                                if (!CommonTool.isCorruptedLink(targetScore.getBeatmap().getBgUrl()))
                                {
                                    imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getBeatmap().getBgUrl());
                                }
                            }
                            else
                            {
                                imageElement.setAttributeNS(xlinkns, "xlink:href", targetScore.getBeatmap().getBgUrl());
                            }
                        }
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
                    doc.getElementById("osu").setAttribute("fill", "#988fcc");
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
                    doc.getElementById("taiko").setAttribute("fill", "#988fcc");
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
                    doc.getElementById("ctb").setAttribute("fill", "#988fcc");
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
                    doc.getElementById("mania").setAttribute("fill", "#988fcc");
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
            grade.setAttribute("fill", rankColorIndictor(targetScore.getRank()));

            doc.getElementById("starRatingBG").setAttribute("fill", "#".concat(CommonTool.calcDiffColor(targetScore.getBeatmap().getDifficult_rating())));

            if (targetScore.getModJSON() != null && targetScore.getModJSON().size() > 0)
            {
                wireModIconForDarkScore(doc,targetScore);
            }
            logger.info("SVG elements changing cost (dark mode): " + (System.currentTimeMillis() - startingTime) + "ms");
            return doc;
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    private static void wireModIconForDarkScore(Document doc, ScoreVO targetScore)
    {
        for (int i = 0; i < targetScore.getModJSON().size(); i++)
        {
            switch (targetScore.getModJSON().get(i).getAcronym().toString())
            {
                case "HR":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "89001f", "a32f4a", "911833");
                    break;
                }
                case "HD":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "bda400", "d3bd58", "c8b02c");
                    break;
                }
                case "DT":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "3a259c", "4f38ab", "45339c");
                    break;
                }
                case "NF":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "0071a5", "237ca5", "157fac");
                    break;
                }
                case "SO":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "3a022d", "5b3055", "511841");
                    break;
                }
                case "FL":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "000100", "303030", "1a1819");
                    break;
                }
                case "SD":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "744c28", "926e4c", "8a5a38");
                    break;
                }
                case "EZ":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "088e47", "38a772", "1c9d58");
                    break;
                }
                case "NC":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "9c22e9", "b925ff", "b520f0");
                    break;
                }
                case "PF":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "e69a4e", "e5a565", "eda25e");
                    break;
                }
                case "HT":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "32323c", "555a5d", "484848");
                    break;
                }
                case "TD":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "27abe3", "4dbcee", "40b3e3");
                    break;
                }
                case "RX":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "27abe3", "4dbcee", "40b3e3");
                    break;
                }
                case "AP":
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "27abe3", "4dbcee", "40b3e3");
                    break;
                }
                case "CL":
                {
                    if(targetScore.getIsLazer())
                    {
                        wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "6b64ab", "766db1", "8077b6");
                        break;
                    }
                    break;
                }
                default:
                {
                    wireModIconForDarkScore(doc, i, targetScore.getModJSON().get(i), "ea629f", "ec75aa", "ee86b4");
                    break;
                }
            }
        }
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
        modBG.setAttribute("fill", "#".concat(color));
        modBG.setAttribute("transform", "skewX(-20)");

        Node modBGNode2 = doc.createElementNS(namespaceSVG, "rect");
        Element modBG2  = (Element) modBGNode2 ;
        modBG2.setAttribute("rx", "10");
        modBG2.setAttribute("ry", "10");
        modBG2.setAttribute("x", "1520");
        modBG2.setAttribute("y", "550");
        modBG2.setAttribute("width", "70");
        modBG2.setAttribute("height", "50");
        modBG2.setAttribute("fill", "#".concat(color2));
        modBG2.setAttribute("transform", "skewX(-20)");

        Node modBGNode3 = doc.createElementNS(namespaceSVG, "rect");
        Element modBG3  = (Element) modBGNode3 ;
        modBG3.setAttribute("rx", "10");
        modBG3.setAttribute("ry", "10");
        modBG3.setAttribute("x", "1470");
        modBG3.setAttribute("y", "530");
        modBG3.setAttribute("width", "50");
        modBG3.setAttribute("height", "30");
        modBG3.setAttribute("fill", "#".concat(color3));
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


        if(mod.getAcronym().equals("DT")||mod.getAcronym().equals("NC")||mod.getAcronym().equals("HT")||mod.getAcronym().equals("DC"))
        {
            Node modClockRateNode = doc.createElementNS(namespaceSVG, "text");
            Element modeClockRate = (Element) modClockRateNode;
            modeClockRate.setAttribute("class", "cls-125");
            modeClockRate.setAttribute("x", "1495");
            modeClockRate.setAttribute("y", "580");
            modeClockRate.setAttribute("transform", "skewX(-10) rotate(-10,1550,1000)");
            if(mod.getSettings().getSpeed_change()!=null) {
                modeClockRate.setTextContent(CommonTool.toString(mod.getSettings().getSpeed_change()).concat("x"));
            }
            sectionFull.appendChild(modClockRateNode);
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
        stop1.setAttributeNS(null, "stop-color", rankColorIndictor(scoreVO.getRank()));
        linearGradient.appendChild(stop1);
        Element stop2 = document.createElementNS(namespaceSVG, "stop");
        if(type==0){
            stop2.setAttributeNS(null, "offset", "0.85");
        }
        else {
            stop2.setAttributeNS(null, "offset", "0.77");
        }
        stop2.setAttributeNS(null, "stop-color", rankColorIndictor(scoreVO.getRank()));
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
            e.printStackTrace();
            throw new RuntimeException(e);
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
        rectBG .setAttribute("fill", getBplistCardModColor(mod));

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



    private static String getBplistCardModColor(Mod mod) {
        switch (mod.getAcronym().trim().toUpperCase()) {
            case "HD":
            case "HR":
            case "FL":
            case "NC":
            case "DT":
            case "SD":
            case "PF":
            case "BL":
            case "ST":
            case "AC":
                return "#ffd810";
            case "EZ":
            case "NF":
            case "HT":
            case "DC":
                return "#ddfbbc";
            case "SO":
            case "RX":
            case "AP":
            case "TD":
                return "#64baff";
            case "CL":
            case "DA":
            case "TP":
            case "RD":
            case "MR":
            case "AL":
            case "SG":
                return "#a066ff";
            default:
                return "#ff8fb1";
        }
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

    private static String rankColorIndictor(String rank)
    {
        switch (rank)
        {
            case "A":
                return "#45e06d";
            case "S":
                return "#ffd25f";
            case "SH":
                return "#e1f4fa";
            case "X":
                return "#ffd464";
            case "XH":
                return "#c9eaf5";
            case "B":
                return "#3baedc";
            case "C":
                return "#9352d5";
            case "D":
                return "#e43350";
            case "F":
                return "#892f2a";
            default:
                return "#ffd25f";
        }
    }
    public static String convertDate(String inputDate) {
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM. d'th', yyyy", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
}
