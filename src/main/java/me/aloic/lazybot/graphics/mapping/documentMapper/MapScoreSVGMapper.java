package me.aloic.lazybot.graphics.mapping.documentMapper;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.mapping.SVGElementHelper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.MapScore;
import me.aloic.lazybot.osu.enums.ModColor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.RankColor;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.util.CommonTool;
import org.spring.osu.extended.rosu.CatchDifficultyAttributes;
import org.spring.osu.extended.rosu.ManiaDifficultyAttributes;
import org.spring.osu.extended.rosu.OsuDifficultyAttributes;
import org.spring.osu.extended.rosu.TaikoDifficultyAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


// used command: /AllScore
public class MapScoreSVGMapper extends LazybotSVGMapper
{
    public static Document mapMapScoreListToAllScorePanel(List<MapScore> scorelist, BeatmapPerformance beatmap, boolean ignoreBanner)
    {
        try
        {
            Document doc = SVGTemplateLoader.loadSVGTemplate("MapScoresPanel");
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


            return setupAllScoreListElement(scorelist, doc, svgRoot, beatmap.getMode(), ignoreBanner);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new LazybotRuntimeException("SVG 处理时出错");
        }
    }
    private static Document setupAllScoreListElement(List<MapScore> scorelist, Document doc, Element svgRoot, OsuMode mode, boolean ignoreBanner)
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

            if(!ignoreBanner) {
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
                sectionFull.appendChild(playerBGImage);
            }

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
            time.setTextContent(SVGElementHelper.convertDate(score.getAchievedTime()));

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
    private static Document setupModIconForAllScores(List<Mod> modList, Document doc, Element sectionFull)
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

    private static String accuracyColorMid(double accuracy) {
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
