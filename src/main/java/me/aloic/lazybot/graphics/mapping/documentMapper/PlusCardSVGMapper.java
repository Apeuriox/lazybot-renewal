package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.enums.PerformanceDimensionLimit;
import me.aloic.lazybot.osu.enums.PerformancePlusTag;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class PlusCardSVGMapper extends LazybotSVGMapper
{
    public static Document mapPlusInfoToCard(PPPlusPerformance performance, PlayerInfoVO player) throws IOException
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("PPplusCard");
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

        List<String> playStyleElements = Arrays.asList(
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
            if (player.getPlayStyles().size()==1 && player.getPlayStyles().getFirst().equalsIgnoreCase("mouse"))
            {
                document.getElementById("input-muse").setAttribute("fill", mainColor.toString());
            }

        }

        setupPPPlusTags(blessPlayerWithTags(performance,average),document,svgRoot,player.getPrimaryColor()-133);
        return document;
    }

    public static Document mapPlusInfoToCardCC2024(PPPlusPerformance performance, PlayerInfoVO player) throws IOException
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("PpPlusCard-CC2024");
        Element svgRoot = document.getDocumentElement();
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        Element defs = document.createElementNS(namespaceSVG, "defs");
        HSL mainColor = new HSL(player.getPrimaryColor(), 80, 35);

        String name=player.getPlayerName();
        //Alought osu username was limited to 15 characters,
        // we still need to check for the possibility of users who registered too early to bypass this limitation.
        if (name.length()>15)
            name=name.substring(0,12).concat("...");
        document.getElementById("name").setTextContent(name);
        if (player.getGlobalRank()!=null)
            document.getElementById("rank").setTextContent("#" + CommonTool.formatNumber(player.getGlobalRank()));
        else{
            document.getElementById("rank").setTextContent("#0");
            document.getElementById("rank").setAttribute("opacity","0.7");
        }

        document.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", player.getAvatarUrl());

        int jumpAim= (int) Math.round(performance.getPpJumpAim());
        int flowAim= (int) Math.round(performance.getPpFlowAim());
        int speed= (int) Math.round(performance.getPpSpeed());
        int stamina= (int) Math.round(performance.getPpStamina());
        int precision= (int) Math.round(performance.getPpPrecision());
        int accuracy= (int) Math.round(performance.getPpAcc());
        int average= (int) Math.round((jumpAim+flowAim+speed+stamina+precision+accuracy)/6);

        double jumpScaled= CommonTool.getScaledRatio(jumpAim, PerformanceDimensionLimit.JUMP.getLimitExpertPlus(), PerformanceDimensionLimit.JUMP.getScaleFactor());
        double flowScaled= CommonTool.getScaledRatio(flowAim, PerformanceDimensionLimit.FLOW.getLimitExpertPlus(), PerformanceDimensionLimit.FLOW.getScaleFactor());
        double speedScaled= CommonTool.getScaledRatio(speed, PerformanceDimensionLimit.SPEED.getLimitExpertPlus(), PerformanceDimensionLimit.SPEED.getScaleFactor());
        double staminaScaled= CommonTool.getScaledRatio(stamina, PerformanceDimensionLimit.STAMINA.getLimitExpertPlus(), PerformanceDimensionLimit.STAMINA.getScaleFactor());
        double precisionScaled= CommonTool.getScaledRatio(precision, PerformanceDimensionLimit.PRECISION.getLimitExpertPlus(), PerformanceDimensionLimit.PRECISION.getScaleFactor());
        double accuracyScaled= CommonTool.getScaledRatio(accuracy, PerformanceDimensionLimit.ACCURACY.getLimitExpertPlus(), PerformanceDimensionLimit.ACCURACY.getScaleFactor());
        double averageScaled= (jumpScaled+flowScaled+speedScaled+staminaScaled+precisionScaled+accuracyScaled)/6.0;



        document.getElementById("jump").setTextContent(String.valueOf(jumpAim));
        document.getElementById("flow").setTextContent(String.valueOf(flowAim));
        document.getElementById("speed").setTextContent(String.valueOf(speed));
        document.getElementById("stamina").setTextContent(String.valueOf(stamina));
        document.getElementById("precision").setTextContent(String.valueOf(precision));
        document.getElementById("accuracy").setTextContent(String.valueOf(accuracy));
        document.getElementById("pp").setTextContent(String.valueOf(Math.round(performance.getPp())));

        document.getElementById("bar-upper").setAttribute("fill", mainColor.toString());
        document.getElementById("bar-lower").setAttribute("fill", mainColor.toString());
        document.getElementById("stats-bg").setAttribute("fill", mainColor.toString());

        document.getElementById("skill-level").setTextContent(String.valueOf(calculateLevel(jumpScaled,flowScaled,speedScaled,staminaScaled,precisionScaled,accuracyScaled)));

        setLinearGradientForCC2024(document,"jump-bar",jumpAim, jumpScaled,  getPrimaryHueForCC2024(jumpScaled,averageScaled,PerformanceDimensionLimit.JUMP,player.getPrimaryColor()));
        setLinearGradientForCC2024(document,"flow-bar",flowAim, flowScaled,  getPrimaryHueForCC2024(flowScaled,averageScaled,PerformanceDimensionLimit.FLOW,player.getPrimaryColor()));
        setLinearGradientForCC2024(document,"speed-bar",speed, speedScaled,  getPrimaryHueForCC2024(speedScaled,averageScaled,PerformanceDimensionLimit.SPEED,player.getPrimaryColor()));
        setLinearGradientForCC2024(document,"stamina-bar",stamina, staminaScaled,  getPrimaryHueForCC2024(staminaScaled,averageScaled,PerformanceDimensionLimit.STAMINA,player.getPrimaryColor()));
        setLinearGradientForCC2024(document,"precision-bar",precision, precisionScaled,  getPrimaryHueForCC2024(precisionScaled,averageScaled,PerformanceDimensionLimit.PRECISION,player.getPrimaryColor()));
        setLinearGradientForCC2024(document,"accuracy-bar",accuracy, accuracyScaled,  getPrimaryHueForCC2024(accuracyScaled,averageScaled,PerformanceDimensionLimit.ACCURACY,player.getPrimaryColor()));


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
            Element tagSingle = doc.createElementNS(namespaceSVG, "g");

            Element rectBG =  doc.createElementNS(namespaceSVG, "rect");
            rectBG.setAttribute("x", "15");
            rectBG.setAttribute("y", "70");
            rectBG.setAttribute("width", String.valueOf(tag.getElementSize()));
            rectBG.setAttribute("height", "20");
            rectBG.setAttribute("fill", new HSL(startHue, 87, 53).toString());

            Element tagName = doc.createElementNS(namespaceSVG, "text");
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
            sectionFull.appendChild(tagSingle);
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
            if (scaled>=0.92) {
                strongCount++;
                PerformancePlusTag tag = mapToMaxTag(dim);
                if (tag != null) tags.add(tag);
            }
            else if (scaled >= (avgScaled * dim.getTagFactor())) {
                PerformancePlusTag tag = mapToTag(dim);
                if (tag != null) tags.add(tag);
            }
            if (scaled> (avgScaled * dim.getTagFactor() * 0.88)) scaledMainTags.add(mapToTag(dim));

        }
        if (strongCount>=5) {
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
        if (tags.isEmpty())
            return List.of(PerformancePlusTag.POTENTIAL);

        return tags.stream().limit(5).sorted().toList();
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
    private static PerformancePlusTag mapToMaxTag(PerformanceDimensionLimit dim)
    {
        return switch (dim) {
            case JUMP -> PerformancePlusTag.SURGICAL;
            case FLOW -> PerformancePlusTag.WORMMASTER;
            case SPEED -> PerformancePlusTag.BLISTERING;
            case STAMINA -> PerformancePlusTag.NUCLEARPOWERED;
            case PRECISION -> PerformancePlusTag.EXQUISITE;
            case ACCURACY -> PerformancePlusTag.VERACIOUS;
            default -> null;
        };
    }
    private static void setLinearGradientForCC2024(Document doc,String elementId, int pp, double scaled, int primaryColor)
    {
        Element svgRoot = doc.getDocumentElement();
        Element linearGradient = doc.createElementNS(namespaceSVG, "linearGradient");
        linearGradient.setAttributeNS(null, "id", "gradient-".concat(elementId));
        linearGradient.setAttributeNS(null, "x1", "106");
        linearGradient.setAttributeNS(null, "y1", "1272");

        linearGradient.setAttributeNS(null, "x2", "1000");//control the width
        linearGradient.setAttributeNS(null, "y2", "1272");
        linearGradient.setAttributeNS(null, "gradientUnits", "userSpaceOnUse");

        String stopColor= new HSL(primaryColor,100,65).toString();
        Element stop1 = doc.createElementNS(namespaceSVG, "stop");
        stop1.setAttributeNS(null, "offset", "0");
        stop1.setAttributeNS(null, "stop-opacity", "1");
        stop1.setAttributeNS(null, "stop-color", stopColor);
        linearGradient.appendChild(stop1);

        Element stop2 = doc.createElementNS(namespaceSVG, "stop");
        stop2.setAttributeNS(null, "offset", String.valueOf(scaled-0.02));
        stop2.setAttributeNS(null, "stop-opacity", "1");
        stop2.setAttributeNS(null, "stop-color", stopColor);
        linearGradient.appendChild(stop2);


        Element stop3 = doc.createElementNS(namespaceSVG, "stop");
        stop3.setAttributeNS(null, "offset", String.valueOf(scaled));
        stop3.setAttributeNS(null, "stop-opacity", "0");
        stop3.setAttributeNS(null, "stop-color", stopColor);
        linearGradient.appendChild(stop3);

        svgRoot.appendChild(linearGradient);
        doc.getElementById(elementId).setAttribute("fill", "url(#".concat("gradient-".concat(elementId))+")");

    }

    private static int getPrimaryHueForCC2024(double scaled, double avgScaled,PerformanceDimensionLimit dim, int primaryColor)
    {
        if(scaled >= (avgScaled * dim.getTagFactor()) || scaled > 0.94)
            return CommonTool.circularHueSubtract(primaryColor,170);
        else
            return primaryColor;
    }
    private static int calculateLevel(double jump, double flow, double speed, double stamina, double precision, double accuracy)
    {
        double[] abilities = {jump, flow, speed, stamina, precision};
        Double[] sorted = Arrays.stream(abilities)
                .boxed()
                .toArray(Double[]::new);
        Arrays.sort(sorted, Collections.reverseOrder());

        double[] weights = {0.35, 0.3, 0.15, 0.12, 0.08};

        double score = 0;
        for (int i = 0; i < 5; i++) {
            score += sorted[i] * weights[i];
        }
        double epsilon = 0.01;
        double lambda = 1.05;

        double penaltyFactor;
        if (accuracy >= score || accuracy>=0.95) {
            penaltyFactor = 1.0;
        } else {
            double delta = score - accuracy;
            penaltyFactor = 1.0 / (1.0 + lambda * (delta / (score + epsilon)));
        }
        double gamma = 0.8;
        double boosted = Math.pow(score, gamma);
        double finalScore = boosted * penaltyFactor;
        int level = (int) Math.round(finalScore * 15);
        return Math.max(1, Math.min(15, level));
    }
}
