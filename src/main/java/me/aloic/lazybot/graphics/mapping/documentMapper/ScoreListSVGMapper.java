package me.aloic.lazybot.graphics.mapping.documentMapper;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.mapping.SVGElementHelper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreSequence;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.ModColor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.RankColor;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class ScoreListSVGMapper extends LazybotSVGMapper
{
    public static Document mapScoreListToBpCard(PlayerInfoVO player, List<ScoreVO> scoreArray, Integer offset, Integer type)
    {
        return mapScoreListToBpCard(player,scoreArray,offset,type,null);
    }
    public static Document mapScoreListToBpCard(PlayerInfoVO player, List<ScoreVO> scoreArray, Integer offset, Integer type, String infoMsg){
        Document document;
        int targetHeight=0;
        if(type==0)
        {
            document = SVGTemplateLoader.loadSVGTemplate("scoreListNew");
            targetHeight = 450 + 350 * ((int) (scoreArray.size() / 3));
            if (scoreArray.size() % 3 == 0)
                targetHeight -= 350;
        }
        else if(type==2||type==3||type==4)
        {
            document = SVGTemplateLoader.loadSVGTemplate("NoChokeScoreCards");
            targetHeight = 450 + 270 * ((int) (scoreArray.size() / 3));
            if (scoreArray.size() % 3 == 0)
                targetHeight -= 270;
        }
        else
        {
            document = SVGTemplateLoader.loadSVGTemplate("scoreListNewTrimed");
            targetHeight = 380 + 270 * ((int) (scoreArray.size() / 3));
            if (scoreArray.size() % 3 == 0)
                targetHeight -= 270;
        }
        Element svgRoot = document.getDocumentElement();

        svgRoot.setAttribute("height", String.valueOf(targetHeight));
        document.getElementById(OsuMode.getMode(scoreArray.getFirst().getMode()).getDescribe()).setAttribute("class", "cls-24");
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
            wireBpListCard(document,scoreArray.get(i),i,scoreArray.size(),offset,type);
        }
        return document;
    }


    private static void wireBpListCard(Document document, ScoreVO scoreVO, int index, int total, int offset, int type)
    {
        Element svgRoot = document.getDocumentElement();
        Element sectionFull = document.createElementNS(namespaceSVG, "g");
        Element defs = document.createElementNS(namespaceSVG, "defs");

        Element listSubSection = document.createElementNS(namespaceSVG, "rect");
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

        Element mapBG = document.createElementNS(namespaceSVG, "image");
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


        Element listSubSectionOpacity  = document.createElementNS(namespaceSVG, "rect");
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

        Element songTitle = document.createElementNS(namespaceSVG, "text");
        String title = scoreVO.getBeatmap().getTitle();
        if (title.length() >= 20) {
            title = title.substring(0, 18) + "...";
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
        Element difficulty = document.createElementNS(namespaceSVG, "text");
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
        String diffTextColor = CommonTool.getStarRatingFontColor(scoreVO.getBeatmap().getDifficult_rating());
        String starType = "assets/osuResources/star-golden.svg";
        if (diffTextColor.equalsIgnoreCase("#1c1719")) {
            starType = "assets/osuResources/star-dark.svg";
        }
        Element star = document.createElementNS(namespaceSVG, "text");;
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

        Element starImage = document.createElementNS(namespaceSVG, "image");
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


        Element acc = document.createElementNS(namespaceSVG, "text");
        acc.setAttribute("class", "cls-44");
        if(type==0){
            acc.setAttribute("transform", "translate(115 306)");
        }
        else{
            acc.setAttribute("transform", "translate(57 250)");
        }
        acc.setTextContent(CommonTool.toString(scoreVO.getAccuracy() * 100).concat("%").concat(" // ").concat(String.valueOf(scoreVO.getMaxCombo())).concat("x"));


        Element ppValue = document.createElementNS(namespaceSVG, "text");
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
            if (scoreVO.getPp()==null) scoreVO.setPp(scoreVO.getPpDetailsLocal().getCurrentPP());
            ppValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())).concat("pp"));
        }

        if(type==2||type==3)
        {
            Element originalPpValue =document.createElementNS(namespaceSVG, "tspan");
            originalPpValue.setAttribute("class", "cls-6");
            originalPpValue.setTextContent(String.valueOf(Math.round(scoreVO.getPpDetailsLocal().getCurrentPP())).concat("pp"));
            ppValue.appendChild(originalPpValue);
            if(Math.abs(scoreVO.getPpDetailsLocal().getCurrentPP()-scoreVO.getPp())>1.5)
            {
                Element arrow = document.createElementNS(namespaceSVG, "tspan");
                arrow.setAttribute("class", "cls-205");
                arrow.setTextContent("->");

                Element fixedPpValue =  document.createElementNS(namespaceSVG, "tspan");
                fixedPpValue.setAttribute("class", "cls-206");
                fixedPpValue.setTextContent(String.valueOf(Math.round(scoreVO.getPp())).concat("pp"));
                ppValue.appendChild(arrow);
                ppValue.appendChild(fixedPpValue);
            }

        }

        Element indexElement = document.createElementNS(namespaceSVG, "text");
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

        Element bid = document.createElementNS(namespaceSVG, "text");
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


        Element acheveTime = document.createElementNS(namespaceSVG, "text");
        acheveTime.setAttribute("class", "cls-1");
        if(type==0) {
            acheveTime.setAttribute("transform", "translate(115 130)");
        }
        else {
            acheveTime.setAttribute("transform", "translate(57 110)");
        }
        acheveTime.setTextContent(SVGElementHelper.convertDate(scoreVO.getCreate_at()));


        String artistAndMapper=scoreVO.getBeatmap().getArtist().concat(" // ").concat(scoreVO.getBeatmap().getCreator());
        if (artistAndMapper.length() >= 35) {
            artistAndMapper = artistAndMapper.substring(0, 34) + "...";
        }
        Element artist = document.createElementNS(namespaceSVG, "text");
        artist.setAttribute("class", "cls-4");
        if(type==0) {
            artist.setAttribute("transform", "translate(115 258)");
        }
        else {
            artist.setAttribute("transform", "translate(57 210)");
        }
        artist.setTextContent(artistAndMapper);

        Element linearGradient = document.createElementNS(namespaceSVG, "linearGradient");
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

        Element line = document.createElementNS(namespaceSVG, "rect");
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
                sectionFull.setAttribute("transform", "translate(" + (135 + index * 270) + " 0)");
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
    private static void appendBpCardModIcon(Document document, Mod mod, Element sectionFull, int index, int panelVersion) {
        Element modSingle = document.createElementNS(namespaceSVG, "g");

        Element rectBG  = document.createElementNS(namespaceSVG, "rect");
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

        Element modAcronym = document.createElementNS(namespaceSVG, "text");
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

        sectionFull.appendChild(modSingle);
    }


    public static Document mapScoreListToBpList(List<ScoreSequence> scorelist, String primaryColor, String type, Integer offset) throws IOException
    {
        try
        {
            Document doc = SVGTemplateLoader.loadSVGTemplate("TopScoresList");
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
            log.error("SVG 处理时出错", e);
            throw new LazybotRuntimeException("SVG 处理时出错");
        }
    }
    public static Document mapScoreListToBpList(List<ScoreSequence> scorelist,PlayerInfoVO info, Integer offset)
    {
        try
        {
            Document doc = SVGTemplateLoader.loadSVGTemplate("ScoreListDetailed");
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
            log.error("SVG 处理时出错", e);
            throw new LazybotRuntimeException("SVG 处理时出错");
        }
    }


    private static Document setupBpListDetailedSingle(List<ScoreSequence> scorelist, String primaryColor, Document doc, Element svgRoot, Integer offset)
    {
        int listIndex=0;
        for (ScoreSequence score : scorelist)
        {
            Element sectionFull = doc.createElementNS(namespaceSVG, "g");
            Element totalBG = doc.createElementNS(namespaceSVG, "rect");
            totalBG.setAttribute("rx", "10");
            totalBG.setAttribute("ry", "10");
            totalBG.setAttribute("width", "950");
            totalBG.setAttribute("height", "100");
            totalBG.setAttribute("fill", "#2a2933");
            totalBG.setAttribute("transform", "translate(30,80)");

            Element mapBGImage = doc.createElementNS(namespaceSVG, "image");
            mapBGImage.setAttributeNS(xlinkns, "xlink:href", score.getBeatmap().getBgUrl());
            mapBGImage.setAttribute("x", "30");
            mapBGImage.setAttribute("y", "80");
            mapBGImage.setAttribute("width", "950");
            mapBGImage.setAttribute("height", "100");
            mapBGImage.setAttribute("opacity", "0.5");
            mapBGImage.setAttribute("clip-path", "url(#singleClip)");
            mapBGImage.setAttribute("preserveAspectRatio", "xMidYMid slice");

            Element totalBGMask = doc.createElementNS(namespaceSVG, "rect");
            totalBGMask.setAttribute("rx", "10");
            totalBGMask.setAttribute("ry", "10");
            totalBGMask.setAttribute("width", "950");
            totalBGMask.setAttribute("height", "100");
            totalBGMask.setAttribute("fill", "#2a2933");
            totalBGMask.setAttribute("opacity", "0.5");
            totalBGMask.setAttribute("transform", "translate(30,80)");

            Element playerName = doc.createElementNS(namespaceSVG, "text");
            playerName.setAttribute("class", "cls-122");
            playerName.setAttribute("transform", "translate(70 170)");
            playerName.setTextContent(score.getPlayerName());

            Element starAndSongTitle = doc.createElementNS(namespaceSVG, "text");
            starAndSongTitle.setAttribute("class", "cls-110");
            starAndSongTitle.setAttribute("transform", "translate(60 125)");

            Element star = doc.createElementNS(namespaceSVG, "tspan");
            star.setAttribute("fill", primaryColor);
            star.setTextContent(CommonTool.toString(score.getBeatmap().getDifficult_rating()).concat(" *"));

            Element divisor = doc.createElementNS(namespaceSVG, "tspan");
            divisor.setTextContent(" | ");

            Element title = doc.createElementNS(namespaceSVG, "tspan");
            String titleAndArtist = score.getBeatmap().getTitle() + " by " + score.getBeatmap().getArtist();
            if (titleAndArtist.length()>60)
                titleAndArtist = titleAndArtist.substring(0, 58).concat("...");
            title.setTextContent(titleAndArtist);


            //pending
//            Node scoreStatusNode = doc.createElementNS(namespaceSVG, "tspan");
//            Element scoreStatus  = (Element) scoreStatusNode;
//            scoreStatus.setAttribute("fill", "#f8bad4");
//            scoreStatus.setTextContent(String.valueOf(score.getBeatmap().getDifficult_rating()).concat(" *"));

            starAndSongTitle.appendChild(star);
            starAndSongTitle.appendChild(divisor);
            starAndSongTitle.appendChild(title);

            Element bpmAndMapper = doc.createElementNS(namespaceSVG, "text");
            bpmAndMapper.setAttribute("class", "cls-113");
            bpmAndMapper.setAttribute("transform", "translate(60 150)");

            Element bpm = doc.createElementNS(namespaceSVG, "tspan");
            bpm.setAttribute("fill", primaryColor);
            bpm.setTextContent(String.valueOf(Math.round(score.getBeatmap().getBpm())).concat(" bpm"));

            Element divisor2 = doc.createElementNS(namespaceSVG, "tspan");
            divisor2.setTextContent(" | ");

            Element mapper = doc.createElementNS(namespaceSVG, "tspan");
            mapper.setTextContent(score.getBeatmap().getCreator().concat(" // [").concat(score.getBeatmap().getVersion()).concat("]"));

            bpmAndMapper.appendChild(bpm);
            bpmAndMapper.appendChild(divisor2);
            bpmAndMapper.appendChild(mapper);

            Element underlineOfDate =  doc.createElementNS(namespaceSVG, "rect");
            underlineOfDate.setAttribute("rx", "1.5");
            underlineOfDate.setAttribute("ry", "1.5");
            underlineOfDate.setAttribute("width", "105");
            underlineOfDate.setAttribute("height", "3");
            underlineOfDate.setAttribute("fill", primaryColor);
            underlineOfDate.setAttribute("transform", "translate(377.5,177)");

            Element underlineOfCombo =  doc.createElementNS(namespaceSVG, "rect");
            underlineOfCombo.setAttribute("rx", "1.5");
            underlineOfCombo.setAttribute("ry", "1.5");
            underlineOfCombo.setAttribute("width", "55");
            underlineOfCombo.setAttribute("height", "3");
            underlineOfCombo.setAttribute("fill", primaryColor);
            underlineOfCombo.setAttribute("transform", "translate(518,177)");

            Element underlineOfAccuracy = doc.createElementNS(namespaceSVG, "rect");
            underlineOfAccuracy.setAttribute("rx", "1.5");
            underlineOfAccuracy.setAttribute("ry", "1.5");
            underlineOfAccuracy.setAttribute("width", "70");
            underlineOfAccuracy.setAttribute("height", "3");
            underlineOfAccuracy.setAttribute("fill", primaryColor);
            underlineOfAccuracy.setAttribute("transform", "translate(605,177)");

            Element underlineOfIndex = doc.createElementNS(namespaceSVG, "rect");
            underlineOfIndex.setAttribute("rx", "1.5");
            underlineOfIndex.setAttribute("ry", "1.5");
            underlineOfIndex.setAttribute("width", "40");
            underlineOfIndex.setAttribute("height", "3");
            underlineOfIndex.setAttribute("fill", primaryColor);
            underlineOfIndex.setAttribute("transform", "translate(707,177)");

            Element underlineOfRank =  doc.createElementNS(namespaceSVG, "rect");
            underlineOfRank.setAttribute("rx", "1.5");
            underlineOfRank.setAttribute("ry", "1.5");
            underlineOfRank.setAttribute("width", "30");
            underlineOfRank.setAttribute("height", "3");
            underlineOfRank.setAttribute("fill", RankColor.fromString(score.getRank()).getDarkRankColorHEX());
            underlineOfRank.setAttribute("transform", "translate(60,80)");

            Element date = doc.createElementNS(namespaceSVG, "text");
            date.setAttribute("class", "cls-111");
            date.setAttribute("font-size", "18px");
            date.setAttribute("text-anchor", "middle");
            date.setAttribute("fill", "#ffffff");
            date.setAttribute("transform", "translate(430 170)");
            date.setTextContent(score.getAchievedTime());

            Element combo =  doc.createElementNS(namespaceSVG, "text");
            combo.setAttribute("class", "cls-111");
            combo.setAttribute("font-size", "18px");
            combo.setAttribute("text-anchor", "middle");
            combo.setAttribute("fill", "#ffffff");
            combo.setAttribute("transform", "translate(545 170)");
            combo.setTextContent(score.getMaxCombo().toString().concat("x"));

            Element accuracy = doc.createElementNS(namespaceSVG, "text");
            accuracy.setAttribute("class", "cls-111");
            accuracy.setAttribute("font-size", "18px");
            accuracy.setAttribute("text-anchor", "middle");
            accuracy.setAttribute("fill", "#ffffff");
            accuracy.setAttribute("transform", "translate(640 170)");
            accuracy.setTextContent(CommonTool.toString(score.getAccuracy() * 100).concat("%"));

            Element index = doc.createElementNS(namespaceSVG, "text");
            index.setAttribute("class", "cls-111");
            index.setAttribute("font-size", "18px");
            index.setAttribute("text-anchor", "middle");
            index.setAttribute("fill", "#ffffff");
            index.setAttribute("transform", "translate(725 170)");
            index.setTextContent("#".concat(String.valueOf((score.getPositionInList()+offset))));

            Element pp = doc.createElementNS(namespaceSVG, "text");
            pp.setAttribute("class", "cls-114");
            pp.setAttribute("text-anchor", "end");
            pp.setAttribute("transform", "translate(960 142)");
            if (score.getPp()==null) score.setPp(score.getPpDetails().getCurrentPP());
            pp.setTextContent(String.valueOf(Math.round(score.getPp())).concat("pp"));

            Element difference = doc.createElementNS(namespaceSVG, "text");

            difference.setAttribute("transform", "translate(910 162)");
            difference.setAttribute("text-anchor", "middle");
            if (score.getDifferenceBetweenNextScore() == null || score.getDifferenceBetweenNextScore() == 0)
            {
                difference.setAttribute("class", "cls-117");
                difference.setTextContent("- pp");
            }
            else if(score.getDifferenceBetweenNextScore()>0)
            {
                difference.setAttribute("class", "cls-115");
                difference.setTextContent("+".concat(String.valueOf(score.getDifferenceBetweenNextScore())).concat("pp"));
            }
            else
            {
                difference.setAttribute("class", "cls-116");
                difference.setTextContent(String.valueOf(score.getDifferenceBetweenNextScore()).concat("pp"));
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
    private static void setupModIconForScoreListDetailed(List<Mod> modList, Document doc, Element sectionFull)
    {
        if (modList.isEmpty()) return;
        modList=modList.reversed();
        for(int i=0;i<modList.size();i++)
        {
            Element modSingle = doc.createElementNS(namespaceSVG, "g");
            Element rectBG = doc.createElementNS(namespaceSVG, "rect");
            rectBG.setAttribute("transform", "translate(925 95)");
            rectBG.setAttribute("rx", "7.5");
            rectBG.setAttribute("ry", "7.5");
            rectBG.setAttribute("width", "30");
            rectBG.setAttribute("height", "15");
            rectBG.setAttribute("fill", ModColor.getModTypeColorHEX(modList.get(i)));

            Element modAcronym = doc.createElementNS(namespaceSVG, "text");
            modAcronym.setAttribute("class", "cls-112");
            modAcronym.setAttribute("transform", "translate(940 107)");
            modAcronym.setAttribute("text-anchor", "middle");
            modAcronym.setTextContent(modList.get(i).getAcronym());

            modSingle.appendChild(rectBG);
            modSingle.appendChild(modAcronym);
            modSingle.setAttribute("transform", "translate(" + -35*i  + " 0)");
            sectionFull.appendChild(modSingle);
        }
    }

}
