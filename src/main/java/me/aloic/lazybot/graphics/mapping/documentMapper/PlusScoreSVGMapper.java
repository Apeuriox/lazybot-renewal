package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.dto.lazybot.LazybotScorePerformance;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

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
}
