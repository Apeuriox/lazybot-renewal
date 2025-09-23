package me.aloic.lazybot.graphics.mapping.documentMapper;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.enums.MoelleuxTypeEnum;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.mapping.SVGElementHelper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoMoelleux;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.ModColor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.PerformanceDimensionLimit;
import me.aloic.lazybot.osu.enums.RankColor;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.osu.theme.preset.ProfileTheme;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class PlayerInfoSVGMapper extends LazybotSVGMapper
{
    public static Document mapPlayerInfoToCard(PlayerInfoVO playerInfoVO)
    {
        try
        {
            int backgroundStyleIndicator=(int)(Math.random()*2);
            Document doc = SVGTemplateLoader.loadSVGTemplate("infoSvgRenewal");
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
        catch (Exception e) {
            log.error(e.getMessage());
            throw new LazybotRuntimeException("Info卡片生成失败");
        }
    }
    public static Document mapPlayerInfoMoelleuxToCard(PlayerInfoMoelleux player, int primaryHue, boolean lowSaturation, boolean enableWhiteMask)
    {
        Document doc = SVGTemplateLoader.loadSVGTemplate("CardMoelleux");
        int saturationFactor = 1;
        if (primaryHue > 360) saturationFactor = 0;
        HSL mainBorderColor;
        HSL lighterOverlayColor;
        HSL ellisColor;
        if (lowSaturation)
        {
            mainBorderColor = new HSL(CommonTool.circularHueSubtract(primaryHue,1), 14*saturationFactor, 56);
            lighterOverlayColor = new HSL(CommonTool.circularHueSubtract(primaryHue,-5), 33*saturationFactor, 98);
            ellisColor = new HSL(CommonTool.circularHueSubtract(primaryHue,2), 13*saturationFactor, 51);
        }
        else
        {
            mainBorderColor = new HSL(CommonTool.circularHueSubtract(primaryHue,2), 44*saturationFactor, 41);
            lighterOverlayColor = new HSL(CommonTool.circularHueSubtract(primaryHue,-5), 33*saturationFactor, 98);
            ellisColor = new HSL(CommonTool.circularHueSubtract(primaryHue,2), 40*saturationFactor, 51);
        }


        if (lowSaturation)
            doc.getElementById("Moelleux").setAttribute("fill", new HSL(primaryHue, 17*saturationFactor, 95).toString());
        else
            doc.getElementById("Moelleux").setAttribute("fill", new HSL(primaryHue, 75*saturationFactor, 95).toString());

        doc.getElementById("footer-bg").setAttribute("fill", mainBorderColor.toString());
        doc.getElementById("renderTime").setTextContent(SVGElementHelper.dateNow());
        String namePlayer =  player.getInfo().getPlayerName().toLowerCase()
                        .replace("-", "")
                .replaceAll("\\d", "");

        player.getInfo().setPlayerName(
                namePlayer.substring(0, 1).toUpperCase()
                        + namePlayer.substring(1)
        );

        doc.getElementById("name-1").setTextContent((player.getInfo().getPlayerName()));
        if(player.getInfo().getPlayerName().length() <= 4)
        {
            doc.getElementById("name-1").setAttribute("text-anchor","middle");
            doc.getElementById("name-1").setAttribute("transform","rotate(90 356 64) translate(940 360)");
        }
        doc.getElementById("name-1").setAttribute("fill", new HSL(primaryHue, 9*saturationFactor, 75).toString());
        doc.getElementById("name-2").setTextContent(player.getInfo().getPlayerName().toUpperCase());

        doc.getElementById("card-bg-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("card-left-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("right-1-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("right-2-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("right-3-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("right-4-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("right-s-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("right-b-border").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("bp-border-1").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("bp-border-2").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("bp-border-3").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("bp-border-4").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("diamonds").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("diamonds2").setAttribute("stroke", mainBorderColor.toString());
        doc.getElementById("avatar-border").setAttribute("stroke", mainBorderColor.toString());

        doc.getElementById("index-bg").setAttribute("fill", mainBorderColor.toString());
        doc.getElementById("lazybot-bg").setAttribute("fill", mainBorderColor.toString());
        doc.getElementById("logo-text").setAttribute("fill", mainBorderColor.toString());


        doc.getElementById("card-left-border").setAttribute("fill", lighterOverlayColor.toString());
        doc.getElementById("right-1-border").setAttribute("fill", lighterOverlayColor.toString());
        doc.getElementById("right-4-border").setAttribute("fill", lighterOverlayColor.toString());
        doc.getElementById("right-b-border").setAttribute("fill", lighterOverlayColor.toString());

        doc.getElementById("avatar-block-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("right-1-label-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("right-2-label-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("right-3-label-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("right-4-label-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("right-s-label-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("right-b-label-bg").setAttribute("fill", ellisColor.toString());
        doc.getElementById("bp-elli-3").setAttribute("fill", ellisColor.toString());
        doc.getElementById("bp-elli-4").setAttribute("fill", ellisColor.toString());

        MoelleuxTypeEnum type = MoelleuxTypeEnum.fromHue(primaryHue);
        doc.getElementById("index").setTextContent("#"+type.ordinal());
        doc.getElementById("card-type").setTextContent(type.getName());
        doc.getElementById("info").setTextContent(type.getName()+": "+player.getInfo().getPlayerName());

        moelleuxBpTitleFontSize(doc,player.getBps());
        doc.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", player.getInfo().getAvatarUrl());
        doc.getElementById("bp-name-1").setTextContent(player.getBps().getFirst().getBeatmap().getCreator().toUpperCase());
        doc.getElementById("bp-name-1").setAttribute("fill",mainBorderColor.toString().toUpperCase());
        doc.getElementById("bp-1").setAttributeNS(xlinkns, "xlink:href", player.getBps().getFirst().getBeatmap().getBgUrl());
        doc.getElementById("bp-layer-border-1").setAttribute("stroke",mainBorderColor.toString());
        doc.getElementById("bp-bid-1").setTextContent(String.valueOf(player.getBps().getFirst().getBeatmap().getBid()));
        doc.getElementById("bp-bid-1").setAttribute("fill",mainBorderColor.toString());

        doc.getElementById("bp-name-2").setTextContent(player.getBps().get(1).getBeatmap().getCreator());
        doc.getElementById("bp-name-2").setAttribute("fill",mainBorderColor.toString().toUpperCase());
        doc.getElementById("bp-2").setAttributeNS(xlinkns, "xlink:href", player.getBps().get(1).getBeatmap().getBgUrl());
        doc.getElementById("right-inner-border-2").setAttribute("stroke",mainBorderColor.toString());
        doc.getElementById("bp-bid-2").setAttribute("fill",mainBorderColor.toString());
        doc.getElementById("bp-bid-2").setTextContent(String.valueOf(player.getBps().get(1).getBeatmap().getBid()));
        doc.getElementById("bp-artist-2").setTextContent(player.getBps().get(1).getBeatmap().getArtist());


        doc.getElementById("bp-name-3").setTextContent(player.getBps().get(2).getBeatmap().getCreator().toUpperCase());
        doc.getElementById("bp-name-3").setAttribute("fill",mainBorderColor.toString());
        doc.getElementById("bp-3").setAttributeNS(xlinkns, "xlink:href", player.getBps().get(2).getBeatmap().getBgUrl());
        doc.getElementById("bp-bid-3").setTextContent(String.valueOf(player.getBps().get(2).getBeatmap().getBid()));
        String artistForBp3=player.getBps().get(2).getBeatmap().getArtist();
        if (artistForBp3.length()>11) artistForBp3=artistForBp3.substring(0,10).concat("...");
        doc.getElementById("bp-artist-3").setTextContent(artistForBp3);

        doc.getElementById("bp-name-4").setAttribute("fill",mainBorderColor.toString());
        doc.getElementById("bp-name-4").setTextContent(player.getBps().get(3).getBeatmap().getCreator().toUpperCase());
        doc.getElementById("bp-4").setAttributeNS(xlinkns, "xlink:href", player.getBps().get(3).getBeatmap().getBgUrl());
        doc.getElementById("bp-bid-4").setTextContent(String.valueOf(player.getBps().get(3).getBeatmap().getBid()));
        String artistForBp4=player.getBps().get(3).getBeatmap().getArtist();
        if (artistForBp4.length()>7) artistForBp4=artistForBp4.substring(0,5).concat("...");
        doc.getElementById("bp-artist-4").setTextContent(artistForBp4);


        doc.getElementById("pp").setTextContent(String.valueOf(Math.round(Optional.ofNullable(player.getInfo().getPerformancePoint()).orElse(0D))));
        doc.getElementById("rank").setTextContent(CommonTool.formatNumber(Optional.ofNullable(player.getInfo().getGlobalRank()).orElse(0)));
        doc.getElementById("playCount").setTextContent(CommonTool.transformNumber(player.getInfo().getPlayCount()));
        doc.getElementById("playTime").setTextContent(CommonTool.formatSecondsToHours(player.getInfo().getTotalPlayTime()).concat("h"));
        doc.getElementById("accuracy").setTextContent(CommonTool.toString(player.getInfo().getAccuracy()).concat("%"));

        setupMoelleuxPPPlus(doc,player, primaryHue, saturationFactor);

        if(!enableWhiteMask) {
            doc.getElementById("bp-1-mask").setAttribute("opacity", "0");
            doc.getElementById("bp-2-mask").setAttribute("opacity", "0");
            doc.getElementById("bp-3-mask").setAttribute("opacity", "0");
            doc.getElementById("bp-4-mask").setAttribute("opacity", "0");
        }
        return doc;
    }
    public static Document mapPlayerInfoMoelleuxToCardTrimmed(PlayerInfoMoelleux player, int primaryHue)
    {
        Document doc = SVGTemplateLoader.loadSVGTemplate("CardMoelleuxTrimmed");
        int saturationFactor = 1;
        if (primaryHue > 360) saturationFactor = 0;
        Element svgRoot = doc.getDocumentElement();
        doc.getElementById("banner").setAttributeNS(xlinkns, "xlink:href", player.getInfo().getBannerUrl());
        doc.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", player.getInfo().getAvatarUrl());

        Node linearGradientNode = doc.createElementNS(namespaceSVG, "linearGradient");
        Element linearGradient = (Element) linearGradientNode;
        linearGradient.setAttributeNS(null, "id", "gradient-1");
        linearGradient.setAttributeNS(null, "x1", "917");
        linearGradient.setAttributeNS(null, "y1", "190");

        linearGradient.setAttributeNS(null, "x2", "0");//control the width
        linearGradient.setAttributeNS(null, "y2", "222");
        linearGradient.setAttributeNS(null, "gradientUnits", "userSpaceOnUse");

        String stopColor= new HSL(primaryHue-22,54*saturationFactor,89).toString();
        String stopColor2= new HSL(primaryHue,54*saturationFactor,76).toString();
        String stopColor3= new HSL(primaryHue-24,97*saturationFactor,85).toString();
        Element stop1 = doc.createElementNS(namespaceSVG, "stop");
        stop1.setAttributeNS(null, "offset", "0");
        stop1.setAttributeNS(null, "stop-opacity", "1");
        stop1.setAttributeNS(null, "stop-color", stopColor);
        linearGradient.appendChild(stop1);

        Element stop2 = doc.createElementNS(namespaceSVG, "stop");
        stop2.setAttributeNS(null, "offset", "0.6");
        stop2.setAttributeNS(null, "stop-opacity", "1");
        stop2.setAttributeNS(null, "stop-color", stopColor2);
        linearGradient.appendChild(stop2);


        Element stop3 = doc.createElementNS(namespaceSVG, "stop");
        stop3.setAttributeNS(null, "offset", "1");
        stop3.setAttributeNS(null, "stop-opacity", "0");
        stop3.setAttributeNS(null, "stop-color", stopColor3);
        linearGradient.appendChild(stop3);

        Node linearGradientNode2 = doc.createElementNS(namespaceSVG, "linearGradient");
        Element linearGradient2 = (Element) linearGradientNode2;
        linearGradient2.setAttributeNS(null, "id", "gradient-2");
        linearGradient2.setAttributeNS(null, "x1", "919");
        linearGradient2.setAttributeNS(null, "y1", "191");

        linearGradient2.setAttributeNS(null, "x2", "0");//control the width
        linearGradient2.setAttributeNS(null, "y2", "234");
        linearGradient2.setAttributeNS(null, "gradientUnits", "userSpaceOnUse");

        String stopColor12= new HSL(primaryHue-18,54*saturationFactor,89).toString();
        String stopColor22= new HSL(primaryHue+14,54*saturationFactor,76).toString();
        String stopColor32= new HSL(primaryHue-17,97*saturationFactor,85).toString();
        Element stop12 = doc.createElementNS(namespaceSVG, "stop");
        stop12.setAttributeNS(null, "offset", "0");
        stop12.setAttributeNS(null, "stop-opacity", "1");
        stop12.setAttributeNS(null, "stop-color", stopColor12);
        linearGradient2.appendChild(stop12);

        Element stop22 = doc.createElementNS(namespaceSVG, "stop");
        stop22.setAttributeNS(null, "offset", "0.6");
        stop22.setAttributeNS(null, "stop-opacity", "1");
        stop22.setAttributeNS(null, "stop-color", stopColor22);
        linearGradient2.appendChild(stop22);


        Element stop32 = doc.createElementNS(namespaceSVG, "stop");
        stop32.setAttributeNS(null, "offset", "1");
        stop32.setAttributeNS(null, "stop-opacity", "0");
        stop32.setAttributeNS(null, "stop-color", stopColor32);
        linearGradient2.appendChild(stop32);

        svgRoot.appendChild(linearGradient2);
        svgRoot.appendChild(linearGradient);

        doc.getElementById("bg-linear-1").setAttribute("fill","url(#gradient-1)");
        doc.getElementById("bg-linear-2").setAttribute("fill","url(#gradient-2)");

        doc.getElementById("bush-line").setAttribute("stroke",new HSL(primaryHue-120,15*saturationFactor,57).toString());
        doc.getElementById("bushi-line-2").setAttribute("stroke",new HSL(primaryHue-120,15*saturationFactor,57).toString());
        doc.getElementById("bush-circle").setAttribute("fill",new HSL(primaryHue-120,15*saturationFactor,57).toString());
        doc.getElementById("bushi-circle-2").setAttribute("fill",new HSL(primaryHue-120,15*saturationFactor,57).toString());
        Random random=new Random();
        doc.getElementById("Bush1").setAttribute("transform","translate(" +  (random.nextInt(20)-10) +"," + (random.nextInt(6)-6) +")");
        doc.getElementById("Bush2").setAttribute("transform","translate(" +  (random.nextInt(20)-10) +"," + (random.nextInt(8)+2) +")");

        HSL fontColor =new HSL(primaryHue,7,51);
        doc.getElementById("circle-1").setAttribute("fill",new HSL(primaryHue-17,97*saturationFactor,88).toString());
        doc.getElementById("circle-2").setAttribute("fill",new HSL(primaryHue+33,96*saturationFactor,90).toString());
        doc.getElementById("circle-3").setAttribute("fill",new HSL(primaryHue-58,77*saturationFactor,76).toString());
        doc.getElementById("date").setTextContent(String.valueOf(LocalDateTime.now().getDayOfMonth()));
        doc.getElementById("name").setTextContent(player.getInfo().getPlayerName());
        doc.getElementById("name").setAttribute("fill",fontColor.toString());
        doc.getElementById("date").setAttribute("fill",fontColor.toString());
        doc.getElementById("team").setAttribute("fill",fontColor.toString());
        doc.getElementById("level").setAttribute("fill",fontColor.toString());
        doc.getElementById("info-rect-bg").setAttribute("fill", "url(#gradient-2)");
        doc.getElementById("team").setTextContent("["+player.getInfo().getTeamShortName()+"]");
        doc.getElementById("level").setTextContent(String.valueOf(player.getInfo().getLevel()));

        doc.getElementById("pp").setTextContent(String.valueOf(Math.round(Optional.ofNullable(player.getInfo().getPerformancePoint()).orElse(0D))));
        doc.getElementById("rank").setTextContent("#" + CommonTool.transformNumber(Optional.ofNullable(player.getInfo().getGlobalRank()).orElse(0)));
        doc.getElementById("pc").setTextContent(CommonTool.transformNumber(player.getInfo().getPlayCount()));
        doc.getElementById("time").setTextContent(CommonTool.formatSecondsToHours(player.getInfo().getTotalPlayTime()).concat("h"));
        doc.getElementById("acc").setTextContent(CommonTool.toString(player.getInfo().getAccuracy()).concat("%"));
        doc.getElementById("logo-rect").setAttribute("fill", String.valueOf(new HSL(primaryHue,17*saturationFactor,60)));

        setupMoelleuxPPPlus(doc,player, primaryHue, saturationFactor);
        return doc;
    }

    private static void setupMoelleuxPPPlus(Document doc, PlayerInfoMoelleux player, int primaryHue, int saturationFactor)
    {
        double jumpScaled= CommonTool.getScaledRatio(player.getPlus().getPpJumpAim(), PerformanceDimensionLimit.JUMP.getLimitExpertPlus(), PerformanceDimensionLimit.JUMP.getScaleFactor());
        double flowScaled= CommonTool.getScaledRatio(player.getPlus().getPpFlowAim(), PerformanceDimensionLimit.FLOW.getLimitExpertPlus(), PerformanceDimensionLimit.FLOW.getScaleFactor());
        double speedScaled= CommonTool.getScaledRatio(player.getPlus().getPpSpeed(), PerformanceDimensionLimit.SPEED.getLimitExpertPlus(), PerformanceDimensionLimit.SPEED.getScaleFactor());
        double staminaScaled= CommonTool.getScaledRatio(player.getPlus().getPpStamina(), PerformanceDimensionLimit.STAMINA.getLimitExpertPlus(), PerformanceDimensionLimit.STAMINA.getScaleFactor());
        double precisionScaled= CommonTool.getScaledRatio(player.getPlus().getPpPrecision(), PerformanceDimensionLimit.PRECISION.getLimitExpertPlus(), PerformanceDimensionLimit.PRECISION.getScaleFactor());
        double accuracyScaled= CommonTool.getScaledRatio(player.getPlus().getPpAcc(), PerformanceDimensionLimit.ACCURACY.getLimitExpertPlus(), PerformanceDimensionLimit.ACCURACY.getScaleFactor());

        doc.getElementById("aim-square").setAttribute("fill", new HSL(primaryHue,50*saturationFactor,50).toString());
        doc.getElementById("aim-value").setTextContent(String.valueOf(Math.round(jumpScaled*15)));
        doc.getElementById("aim-label").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,-32),50*saturationFactor,20).toString());
        doc.getElementById("aim-value").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,-30),51*saturationFactor,23).toString());


        doc.getElementById("flow-square-1").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,-35),50*saturationFactor,60).toString());
        doc.getElementById("flow-square-2").setAttribute("stroke", new HSL(CommonTool.circularHueSubtract(primaryHue,-35),50*saturationFactor,60).toString());
        doc.getElementById("flow-value").setTextContent(String.valueOf(Math.round(flowScaled*15)));
        doc.getElementById("flow-label").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,-53),50*saturationFactor,20).toString());
        doc.getElementById("flow-value").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,-53),51*saturationFactor,23).toString());


        doc.getElementById("pre-square-1").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,18),14*saturationFactor,68).toString());
        doc.getElementById("pre-square-2").setAttribute("stroke", new HSL(CommonTool.circularHueSubtract(primaryHue,20),15*saturationFactor,84).toString());
        doc.getElementById("pre-value").setTextContent(String.valueOf(Math.round(precisionScaled*15)));
        doc.getElementById("pre-label").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,18),50*saturationFactor,20).toString());
        doc.getElementById("pre-value").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,18),51*saturationFactor,23).toString());


        doc.getElementById("speed-square").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,82),46*saturationFactor,83).toString());
        doc.getElementById("speed-square").setAttribute("stroke", new HSL(CommonTool.circularHueSubtract(primaryHue,101),46*saturationFactor,60).toString());
        doc.getElementById("spd-value").setTextContent(String.valueOf(Math.round(speedScaled*15)));
        doc.getElementById("spd-label").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,83),50*saturationFactor,20).toString());
        doc.getElementById("spd-value").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,83),51*saturationFactor,23).toString());


        doc.getElementById("sta-square-1").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,128),54*saturationFactor,91).toString());
        doc.getElementById("sta-square-2").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,123),60*saturationFactor,80).toString());
        doc.getElementById("sta-value").setTextContent(String.valueOf(Math.round(staminaScaled*15)));
        doc.getElementById("sta-label").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,132),50*saturationFactor,20).toString());
        doc.getElementById("sta-value").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,132),51*saturationFactor,23).toString());

        doc.getElementById("acc-square-1").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,193),54*saturationFactor,86).toString());
        doc.getElementById("acc-square-2").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,192),54*saturationFactor,93).toString());
        doc.getElementById("acc-value").setTextContent(String.valueOf(Math.round(accuracyScaled*15)));
        doc.getElementById("acc-label").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,184),50*saturationFactor,20).toString());
        doc.getElementById("acc-value").setAttribute("fill", new HSL(CommonTool.circularHueSubtract(primaryHue,184),51*saturationFactor,23).toString());

    }




    public static Document mapPlayerInfoToProfilePanel(PlayerInfoVO playerInfo, ProfileTheme theme) throws IOException
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("InfoV2-WhiteSpace");
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        document.getElementById("playername").setTextContent(playerInfo.getPlayerName());
        document.getElementById("requestTime").setTextContent(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        document.getElementById("countryAbbrv").setTextContent(playerInfo.getCountryCode());
        document.getElementById("countryRank").setTextContent(String.valueOf(playerInfo.getCountryRank()));
        if (playerInfo.getGlobalRank()==null) playerInfo.setGlobalRank(0);
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
            log.info("Profile rank history invalid, using default rank history");
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
    private static void moelleuxBpTitleFontSize(Document doc, List<ScoreVO> scores)
    {
        for (int i=0;i<4;i++)
        {
            String title= scores.get(i).getBeatmap().getTitle();
            if (title.length()>15) {
                title= title.substring(0,13)+"...";
            }
            if (title.length()>11) {
                doc.getElementById("bp-title-"+(i+1)).setAttribute("font-size","25");
            }
            if (title.length()<=11) {
                doc.getElementById("bp-title-"+(i+1)).setAttribute("font-size","28");
            }
            if (title.length()<8) {
                doc.getElementById("bp-title-"+(i+1)).setAttribute("font-size","30");
            }
            if (title.length()<5) {
                doc.getElementById("bp-title-"+(i+1)).setAttribute("font-size","35");
            }
            doc.getElementById("bp-title-"+(i+1)).setTextContent(title);
        }

    }
}
