package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusScore;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.enums.PPPlusIncompatibleMods;
import me.aloic.lazybot.osu.enums.RankColor;
import me.aloic.lazybot.osu.enums.RankedMods;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

public class PlusScoreSVGMapper extends LazybotSVGMapper
{
    public static Document mapPlusScoreToCard(LazybotScorePerformance score, ScoreVO scoreVO, Integer hue) throws IOException
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("PlusScoreCrimson");
        Element svgRoot = document.getDocumentElement();
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        HSL mainColor = new HSL(hue, 85, 60);
        HSL darkColor = new HSL(hue, 35, 35);
        HSL mainBarColor = new HSL(CommonTool.circularHueSubtract(hue, -5), 77, 57);
        HSL mainDarkBarColor = new HSL(CommonTool.circularHueSubtract(hue, -5), 37, 38);
        HSL numberColor = new HSL(CommonTool.circularHueSubtract(hue, -18), 78, 78);


        document.getElementById("playername").setTextContent(scoreVO.getUser_name().toUpperCase());
        document.getElementById("scoreid").setTextContent(String.valueOf(score.getScoreId()));
        document.getElementById("header-bg").setAttribute("fill", mainColor.toString());
        document.getElementById("mapBG").setAttributeNS(xlinkns, "xlink:href", scoreVO.getBeatmap().getBgUrl());

        String title=scoreVO.getBeatmap().getTitle();
        if (title.length()>17) title=title.substring(0,16)+"...";

        String artist=scoreVO.getBeatmap().getArtist();
        if (artist.length()>21) artist=artist.substring(0,20)+"...";

        String version=scoreVO.getBeatmap().getVersion();
        if (version.length()>10) version=version.substring(0,9)+"...";

        String mapper=scoreVO.getBeatmap().getCreator();
        if (mapper.length()>10) mapper=mapper.substring(0,9)+"...";

        document.getElementById("title").setTextContent(title);
        document.getElementById("artist").setTextContent(artist);
        document.getElementById("version").setTextContent(version);
        document.getElementById("mapper").setTextContent(mapper);
        document.getElementById("divisor").setAttribute("fill", darkColor.toString());
        document.getElementById("mapper-bg").setAttribute("fill", mainColor.toString());
        document.getElementById("lower-bg").setAttribute("fill", mainColor.toString());


        Node radialGradientNode = document.createElementNS(namespaceSVG, "radialGradient");
        Element radialGradient = (Element) radialGradientNode;
        radialGradient.setAttributeNS(null, "id", "gradient1");
        radialGradient.setAttributeNS(null, "cx", "0");
        radialGradient.setAttributeNS(null, "cy", "0");
        radialGradient.setAttributeNS(null, "r", "1");
        radialGradient.setAttributeNS(null, "gradientTransform", "matrix(-145.5,-2236,3062.73,-199.297,2000,2576)");
        radialGradient.setAttributeNS(null, "gradientUnits", "userSpaceOnUse");

        String stopColor= new HSL(hue,28,25).toString();
        Element stop1 = document.createElementNS(namespaceSVG, "stop");
        stop1.setAttributeNS(null, "offset", "0");
        stop1.setAttributeNS(null, "stop-opacity", "0.8");
        stop1.setAttributeNS(null, "stop-color", stopColor);
        radialGradient.appendChild(stop1);

        Element stop2 = document.createElementNS(namespaceSVG, "stop");
        stop2.setAttributeNS(null, "offset", "0.82");
        stop2.setAttributeNS(null, "stop-opacity", "0");
        stop2.setAttributeNS(null, "stop-color", stopColor);
        radialGradient.appendChild(stop2);

        svgRoot.appendChild(radialGradient);

        document.getElementById("lower-linear").setAttribute("fill", "url(#gradient1)");

        //1650 max
        double jumpScaled= score.getPpJump()/score.getPp();
        double flowScaled= score.getPpFlow()/score.getPp();
        double speedScaled= score.getPpSpeed()/score.getPp();
        double staminaScaled= score.getPpStamina()/score.getPp();
        double precisionScaled= score.getPpPrecision()*10/score.getPp();
        double accuracyScaled= score.getPpAccuracy()/score.getPp();

        document.getElementById("jump").setTextContent(String.valueOf(Math.round(score.getPpJump())));
        document.getElementById("flow").setTextContent(String.valueOf(Math.round(score.getPpFlow())));
        document.getElementById("speed").setTextContent(String.valueOf(Math.round(score.getPpSpeed())));
        document.getElementById("stamina").setTextContent(String.valueOf(Math.round(score.getPpStamina())));
        document.getElementById("precision").setTextContent(String.valueOf(Math.round(score.getPpPrecision()*10)));
        document.getElementById("accuracy").setTextContent(String.valueOf(Math.round(score.getPpAccuracy())));
        document.getElementById("pp").setTextContent(String.valueOf(Math.round(score.getPp())));

        document.getElementById("jump").setAttribute("fill", numberColor.toString());
        document.getElementById("flow").setAttribute("fill", numberColor.toString());
        document.getElementById("speed").setAttribute("fill", numberColor.toString());
        document.getElementById("stamina").setAttribute("fill", numberColor.toString());
        document.getElementById("precision").setAttribute("fill", numberColor.toString());
        document.getElementById("accuracy").setAttribute("fill", numberColor.toString());
        document.getElementById("pp").setAttribute("fill", numberColor.toString());

        document.getElementById("type").setTextContent(evaluateType(jumpScaled,flowScaled,speedScaled,staminaScaled,precisionScaled));

        document.getElementById("jump-bar").setAttribute("width", 1650*jumpScaled+"");
        document.getElementById("flow-bar").setAttribute("width", 1650*flowScaled+"");
        document.getElementById("speed-bar").setAttribute("width", 1650*speedScaled+"");
        document.getElementById("stamina-bar").setAttribute("width", 1650*staminaScaled+"");
        document.getElementById("precision-bar").setAttribute("width", 1650*precisionScaled+"");
        document.getElementById("accuracy-bar").setAttribute("width", 1650*accuracyScaled+"");

        document.getElementById("jump-bar").setAttribute("fill", mainBarColor.toString());
        document.getElementById("flow-bar").setAttribute("fill", mainBarColor.toString());
        document.getElementById("speed-bar").setAttribute("fill", mainBarColor.toString());
        document.getElementById("stamina-bar").setAttribute("fill", mainBarColor.toString());
        document.getElementById("precision-bar").setAttribute("fill", mainBarColor.toString());
        document.getElementById("accuracy-bar").setAttribute("fill", mainBarColor.toString());

        document.getElementById("jump-bar-inner").setAttribute("fill", mainDarkBarColor.toString());
        document.getElementById("flow-bar-inner").setAttribute("fill", mainDarkBarColor.toString());
        document.getElementById("speed-bar-inner").setAttribute("fill", mainDarkBarColor.toString());
        document.getElementById("stamina-bar-inner").setAttribute("fill", mainDarkBarColor.toString());
        document.getElementById("precision-bar-inner").setAttribute("fill", mainDarkBarColor.toString());
        document.getElementById("accuracy-bar-inner").setAttribute("fill", mainDarkBarColor.toString());

        return document;
    }

    public static Document mapPlusScoreToQuadraGrid(PPPlusScore score, Integer hue) throws IOException
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("PlusScoreQuadraGrid");
        HSL plusRectColor = new HSL(hue, 28, 93);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        OffsetDateTime odt = OffsetDateTime.parse(score.getCreate_at(), inputFormatter);

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss");
        document.getElementById("playername").setTextContent(score.getUser_name());
        document.getElementById("time").setTextContent(odt.toLocalDateTime().format(outputFormatter));

        if (score.getModJSON() != null && !score.getModJSON().isEmpty()) {
            if (!PPPlusIncompatibleMods.checkModsCompatibility(score.getModJSON()))
                document.getElementById("InCompatible Notice").setAttribute("opacity","1");
            if (!score.getIsLazer())
                score.setModJSON(
                        score.getModJSON().stream()
                                .filter(mod -> !mod.getAcronym()
                                        .equalsIgnoreCase("CL"))
                                .toList());
        }
        String modStr = null;
        if (score.getModJSON() != null)
            modStr = score.getModJSON().stream()
                    .map(Mod::getAcronym)
                    .collect(Collectors.joining());

        if (modStr == null || modStr.isEmpty())
            document.getElementById("mod").setTextContent("Nomod Play");
        else document.getElementById("mod").setTextContent("+" + modStr);

        if (score.getIsLazer())
             document.getElementById("client").setTextContent("Lazer");


        document.getElementById("300").setTextContent(String.valueOf(score.getStatistics().getGreat()));
        document.getElementById("100").setTextContent(String.valueOf(score.getStatistics().getOk()));
        document.getElementById("50").setTextContent(String.valueOf(score.getStatistics().getMeh()));
        document.getElementById("miss").setTextContent(String.valueOf(score.getStatistics().getMiss()));
        document.getElementById("combo").setTextContent(CommonTool.toString(score.getMaxCombo()).concat("x/")
                .concat(CommonTool.toString(score.getBeatmap().getMax_combo())
                        .concat("x")).concat(" (").
                concat(CommonTool.toString(((double) score.getMaxCombo() / (double) score.getBeatmap().getMax_combo()) * 100.0).concat("%)")));
        document.getElementById("accuracy").setTextContent(CommonTool.toString(score.getAccuracy()*100) +"%");
        document.getElementById("rank").setTextContent(score.getRank());
        document.getElementById("rankRect").setAttribute("fill", RankColor.fromString(score.getRank()).getDarkRankColorHEX());
        if (score.getRank().length()>1) {
            document.getElementById("rankRect").setAttribute("x", "95");
        }
        document.getElementById(score.getBeatmap().getStatus()).setAttribute("opacity", "1");

        document.getElementById("mapBG").setAttributeNS(xlinkns, "xlink:href", score.getBeatmap().getBgUrl());
        document.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", score.getAvatarUrl());

        document.getElementById("ppp-rect-1").setAttribute("fill",plusRectColor.toString());
        document.getElementById("ppp-rect-2").setAttribute("fill",plusRectColor.toString());
        document.getElementById("jump-ppp").setTextContent(concatValueString(score.getPlusPerformance().getPpJumpAim(), score.getMaxPerformance().getPpJumpAim()));
        document.getElementById("flow-ppp").setTextContent(concatValueString(score.getPlusPerformance().getPpFlowAim(), score.getMaxPerformance().getPpFlowAim()));
        document.getElementById("spd-ppp").setTextContent(concatValueString(score.getPlusPerformance().getPpSpeed(), score.getMaxPerformance().getPpSpeed()));
        document.getElementById("sta-ppp").setTextContent(concatValueString(score.getPlusPerformance().getPpStamina(), score.getMaxPerformance().getPpStamina()));
        document.getElementById("pre-ppp").setTextContent(concatValueString(score.getPlusPerformance().getPpPrecision()*10.0, score.getMaxPerformance().getPpPrecision()*10.0));
        document.getElementById("acc-ppp").setTextContent(concatValueString(score.getPlusPerformance().getPpAcc(), score.getMaxPerformance().getPpAcc()));
        document.getElementById("pp-ppp").setTextContent(Math.round(score.getPlusPerformance().getPp())+"pp");
        document.getElementById("fc-ppp").setTextContent(Math.round(score.getPlusPerformance().getIffc())+"pp");



        document.getElementById("aim-pp").setTextContent(concatValueString(score.getPpDetailsLocal().getAimPP(), score.getPpDetailsLocal().getAimPPMax()));
        document.getElementById("spd-pp").setTextContent(concatValueString(score.getPpDetailsLocal().getSpdPP(), score.getPpDetailsLocal().getSpdPPMax()));
        document.getElementById("acc-pp").setTextContent(concatValueString(score.getPpDetailsLocal().getAccPP(), score.getPpDetailsLocal().getAccPPMax()));
        document.getElementById("pp-pp").setTextContent(Math.round(score.getPp())+"pp");
        document.getElementById("fc-pp").setTextContent(Math.round(score.getPpDetailsLocal().getIfFc())+"pp");

        String titleAndArtist=score.getBeatmap().getArtist().concat(" - ").concat(score.getBeatmap().getTitle());
        if (titleAndArtist.length()>46) titleAndArtist=titleAndArtist.substring(0,44)+"...";
        document.getElementById("titleAndArtist").setTextContent(titleAndArtist);
        document.getElementById("version").setTextContent(score.getBeatmap().getVersion());
        document.getElementById("mapper").setTextContent(score.getBeatmap().getCreator());
        document.getElementById("bid").setTextContent(String.valueOf(score.getBeatmap().getBid()));

        document.getElementById("bpm").setTextContent(CommonTool.toString(score.getBeatmap().getAttributes().getBpm()));
        document.getElementById("length").setTextContent(CommonTool.formatHitLength(score.getBeatmap().getAttributes().getLength()));
        document.getElementById("ar").setTextContent(CommonTool.toString(score.getBeatmap().getAttributes().getAr()));
        document.getElementById("od").setTextContent(CommonTool.toString(score.getBeatmap().getAttributes().getOd()));
        document.getElementById("hp").setTextContent(CommonTool.toString(score.getBeatmap().getAttributes().getHp()));
        document.getElementById("cs").setTextContent(CommonTool.toString(score.getBeatmap().getAttributes().getCs()));

        document.getElementById("star-1").setTextContent(CommonTool.toString(score.getBeatmap().getDifficult_rating()));
        document.getElementById("star-2").setTextContent(CommonTool.toString(score.getBeatmap().getDifficult_rating()));
        String diffColor="#".concat(CommonTool.calcDiffColor(score.getBeatmap().getDifficult_rating()));
        String starTextColor = "#fed867";
        if (score.getBeatmap().getDifficult_rating() < 7.0 && score.getBeatmap().getDifficult_rating() % 1.0 < 0.5)
            starTextColor = "#1c1719";
        document.getElementById("star-2").setAttribute("fill", starTextColor);
        document.getElementById("star-rect").setAttribute("fill", diffColor);


        return document;
    }
    private static String evaluateType(double jump, double flow, double speed, double stamina, double precision)
    {
        double[] values = {jump, flow, speed, stamina, precision};
        String[] labels = {"AIM/SHARP", "AIM/FLOW", "TAP/SPEED", "TAP/STAMINA", "TECH/PRECISION"};
        double[] correctedValues = {0.81, 1.02, 1.15, 1.24, 1.4};
        for (int i = 0; i < 5; i++) {
            values[i] = values[i] * correctedValues[i];
        }
        int maxIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] > values[maxIndex]) {
                maxIndex = i;
            }
        }

        return labels[maxIndex];
    }
    private static String concatValueString(double current, double max)
    {
        return Math.round(current) + "/"
                + (Math.round(max)) + " (".
                concat(CommonTool.toString((current / max) * 100.0).concat("%)"));
    }
}
