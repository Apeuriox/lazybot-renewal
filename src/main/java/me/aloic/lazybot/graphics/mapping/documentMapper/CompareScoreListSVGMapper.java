package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.ModColor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class CompareScoreListSVGMapper extends LazybotSVGMapper
{
    public static Document mapScoresToCompareScoreList(PlayerInfoDTO currentPlayer, PlayerInfoDTO comparedPlayer,
                                               ScoreVO[] scoreVOArray, ScoreVO[] compareScoreVOArray) throws IOException
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("scoreListCompareFull");
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
                createCompareListSubSection(document,allScores[i],i,0);
            }
            else
            {
                createCompareListSubSection(document,allScores[i],i,1);
            }
        }
        return document;
    }
    private static void createCompareListSubSection(Document document, ScoreVO scoreVO, int index, int type)
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

}
