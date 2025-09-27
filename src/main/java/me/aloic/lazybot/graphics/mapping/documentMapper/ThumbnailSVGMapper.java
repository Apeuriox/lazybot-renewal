package me.aloic.lazybot.graphics.mapping.documentMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.enums.MoelleuxTypeEnum;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.vo.BeatmapVO;
import me.aloic.lazybot.osu.enums.ModColor;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.osu.enums.RankColor;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ThumbnailSVGMapper extends LazybotSVGMapper
{
    public static Document mapToThumbnailClassical(ThumbnailClassicalVO data)
    {
        Document doc = SVGTemplateLoader.loadSVGTemplate("ThumbnailClassical");

        doc.getElementById("bg").setAttributeNS(xlinkns,
                "xlink:href", data.getScore().getBeatmap().getBgUrl());
        doc.getElementById("bg-header").setAttributeNS(xlinkns,
                "xlink:href", data.getScore().getBeatmap().getBgUrl());
        doc.getElementById("bg-avatar").setAttributeNS(xlinkns,
                "xlink:href", data.getScore().getBeatmap().getBgUrl());
        doc.getElementById("avatar").setAttributeNS(xlinkns,
                "xlink:href", data.getPlayer().getAvatarUrl());
        String namePlayer =  data.getPlayer().getPlayerName();
        if (namePlayer.length()>8)
            namePlayer = namePlayer.substring(0, 7) + "...";
        doc.getElementById("name").setTextContent(namePlayer);

        doc.getElementById("area").setTextContent(data.getPlayer().getCountryCode());
        if (data.getPlayer().getTeamShortName()==null)
        {
            doc.getElementById("team-block").setAttribute("opacity","0");
            doc.getElementById("team").setAttribute("opacity","0");
        }
        else
            doc.getElementById("team").setTextContent(data.getPlayer().getTeamShortName());


        if (data.getScore().getPp()>9999)
            data.getScore().setPp(9999D);
        doc.getElementById("pp").setTextContent(Math.round(data.getScore().getPp())+"PP");
        doc.getElementById("ranking").setTextContent("#"+Optional.ofNullable(data.getPosition()).orElse("-"));
        doc.getElementById("accuracy").setTextContent(CommonTool.toString(data.getScore().getAccuracy()*100.0)+"%");
        doc.getElementById("combo").setTextContent(CommonTool.toString(data.getScore().getMaxCombo())+"X");
        doc.getElementById("pp-s").setTextContent(Math.round(data.getScore().getPp())+"PP");
        doc.getElementById("ranking-s").setTextContent("#"+Optional.ofNullable(data.getPosition()).orElse("-"));
        doc.getElementById("accuracy-s").setTextContent(CommonTool.toString(data.getScore().getAccuracy()*100)+"%");
        doc.getElementById("combo-s").setTextContent(CommonTool.toString(data.getScore().getMaxCombo())+"X");
        setUpMissMessage(doc,data);

        //attr
        distributeBeatmapAttributes(data.getAttributes(),doc,data.getScore().getBeatmap());


        String diffColor = "#" + CommonTool.calcDiffColor(data.getScore().getBeatmap().getDifficult_rating());
        String versionColor = "#1c1719";
        if (data.getScore().getBeatmap().getDifficult_rating() < 7.0)
        {
            if (data.getScore().getBeatmap().getDifficult_rating() % 1.0 > 0.5)
                versionColor = "#fed867";
        }
        else
            versionColor = "#fed867";
        if (data.getScore().getBeatmap().getDifficult_rating() < 3.0)
            versionColor = "#1c1719";
        doc.getElementById("title").setTextContent(data.getScore().getBeatmap().getTitle());
        int titleFontSize = Math.min(100, 100 + (33 - data.getScore().getBeatmap().getTitle().length()));
        doc.getElementById("title").setAttribute("font-size", String.valueOf(titleFontSize));
        doc.getElementById(OsuMode.convertMode(data.getScore().getMode()).getDescribe()).setAttribute("opacity","1");
        try{
            doc.getElementById(data.getScore().getBeatmap().getStatus()).setAttribute("opacity", "1");
        }
        catch (Exception e)
        {

        }
        doc.getElementById("star-bg").setAttribute("fill", diffColor);
        doc.getElementById("version-dot").setAttribute("fill", versionColor);
        doc.getElementById("version").setAttribute("fill", versionColor);
        doc.getElementById("star").setAttribute("fill", versionColor);
        doc.getElementById("starrating").setAttribute("fill", versionColor);
        doc.getElementById("starrating").setTextContent(CommonTool.toString(data.getScore().getBeatmap().getDifficult_rating()));

        String version= data.getScore().getBeatmap().getVersion();
        int starBgRectWidth = 630;
        if (version.length()>9)
        {
            version = version.substring(0, 8) + "...";
        }
        else
        {
            starBgRectWidth = 630 - (9-version.length())*40;
        }
        doc.getElementById("star-bg").setAttribute("width", String.valueOf(starBgRectWidth));
        doc.getElementById("version").setTextContent(version);
        //mods
        distributeMods(doc,data.getScore().getModJSON());


        doc.getElementById("comment").setTextContent(data.getComment());

        return doc;
    }
    private static void setUpMissMessage(Document doc, ThumbnailClassicalVO data)
    {
        if (data.getScore().getStatistics().getMiss()>=1 && data.getScore().getStatistics().getMiss()<100)
        {
            doc.getElementById("miss").setTextContent(data.getScore().getStatistics().getMiss()+"X");
            doc.getElementById("miss-s").setTextContent(data.getScore().getStatistics().getMiss()+"X");
        }
        else if (data.getScore().getStatistics().getMiss()>=100)
        {
            doc.getElementById("miss").setTextContent("PASS");
            doc.getElementById("miss-s").setTextContent("PASS");
            doc.getElementById("miss").setAttribute("fill", "#FFD633");
        }
        else if (data.getScore().getStatistics().getMiss()==0)
        {
            if ((double) data.getScore().getMaxCombo() /data.getScore().getBeatmap().getMax_combo()>=0.96)
            {
                doc.getElementById("miss").setTextContent("FC");
                doc.getElementById("miss-s").setTextContent("FC");
                doc.getElementById("miss").setAttribute("fill", "#FFD633");
            }
            else
            {
                doc.getElementById("miss").setTextContent("0X");
                doc.getElementById("miss-s").setTextContent("0X");
                doc.getElementById("miss").setAttribute("fill", "#abb0c0");
            }
        }
    }
    private static void distributeBeatmapAttributes(List<ThumbnailClassicalAttribute> attrs, Document doc, BeatmapVO beatmap)
    {
        int offset = 0;
        for (ThumbnailClassicalAttribute attr : attrs)
        {
            switch (attr)
            {
                case AR:
                    setupBeatmapAttributes(doc, attr, CommonTool.toString(beatmap.getAttributes().getAr(),1), offset);
                    offset-= attr.getOffset();
                    break;
                case OD:
                    setupBeatmapAttributes(doc, attr, CommonTool.toString(beatmap.getAttributes().getOd(),1), offset);
                    offset-= attr.getOffset();
                    break;
                case CS:
                    setupBeatmapAttributes(doc, attr, CommonTool.toString(beatmap.getAttributes().getCs(),1), offset);
                    offset-= attr.getOffset();
                    break;
                case HP:
                    setupBeatmapAttributes(doc, attr, CommonTool.toString(beatmap.getAttributes().getHp(),1), offset);
                    offset-= attr.getOffset();
                    break;
                case BPM:
                    setupBeatmapAttributes(doc, attr, String.valueOf(Math.round(beatmap.getAttributes().getBpm())), offset);
                    offset-= attr.getOffset();
                    break;
                case LENGTH:
                    setupBeatmapAttributes(doc, attr, CommonTool.formatHitLength(beatmap.getAttributes().getLength()), offset);
                    offset-= attr.getOffset();
                    break;
            }
        }
    }
    private static void setupBeatmapAttributes(Document doc, ThumbnailClassicalAttribute type , String attrValue, int offset)
    {
        int difference = type.getOffset()-220;
        Element group = doc.createElementNS(namespaceSVG, "g");
        group.setAttribute("transform", "translate(" + offset +",0)");
        group.setAttribute("clip-path", "url(#attrClip-" + type.getWidthOfLeft() +")");
        Element svgRoot = doc.getDocumentElement();
        Element backgroundRect1 = doc.createElementNS(namespaceSVG, "rect");
        backgroundRect1.setAttribute("x", String.valueOf(1618-difference));
        backgroundRect1.setAttribute("y", "560");
        backgroundRect1.setAttribute("width", String.valueOf(type.getWidthOfLeft()));
        backgroundRect1.setAttribute("height", "80");
        backgroundRect1.setAttribute("fill", type.getHex());


        Element backgroundRect2 = doc.createElementNS(namespaceSVG, "rect");
        backgroundRect2.setAttribute("x", String.valueOf(1618-difference+type.getWidthOfLeft()));
        backgroundRect2.setAttribute("y", "560");
        backgroundRect2.setAttribute("width", String.valueOf(type.getOffset()-20-type.getWidthOfLeft()));
        backgroundRect2.setAttribute("height", "80");
        backgroundRect2.setAttribute("fill", "#ffffff");

        Element label = doc.createElementNS(namespaceSVG, "text");
        label.setAttribute("class", "cls-1");
        label.setAttribute("font-weight", "600");
        label.setAttribute("font-size", "50");
        label.setAttribute("x", String.valueOf(1640-difference));
        label.setAttribute("y", "616");
        label.setAttribute("fill", "#ffffff");
        label.setTextContent(type.name().toLowerCase());

        Element value = doc.createElementNS(namespaceSVG, "text");
        value.setAttribute("class", "cls-1");
        value.setAttribute("font-weight", "600");
        value.setAttribute("font-size", "50");
        value.setAttribute("text-anchor", "middle");
        value.setAttribute("x", String.valueOf(1768-difference*0.25));
        value.setAttribute("y", "616");
        value.setAttribute("fill", "#333333");
        value.setTextContent(attrValue);

        group.appendChild(backgroundRect1);
        group.appendChild(backgroundRect2);
        group.appendChild(label);
        group.appendChild(value);
        svgRoot.appendChild(group);
    }
    private static void distributeMods(Document doc, List<Mod> mods)
    {
        int offset = 0;
        if (mods==null || mods.isEmpty())
        {
            setupScoreMods(doc, new Mod("NM",null), offset);
        }
        else
        {
            for (Mod mod : mods)
            {
                setupScoreMods(doc, mod, offset);
                offset-=160;
            }
        }
    }
    private static void setupScoreMods(Document doc, Mod mod, int offset)
    {
        Element group = doc.createElementNS(namespaceSVG, "g");
        group.setAttribute("transform", "translate(" + offset +",0)");

        Element svgRoot = doc.getDocumentElement();

        Element backgroundRect = doc.createElementNS(namespaceSVG, "rect");
        backgroundRect.setAttribute("x", "1670");
        backgroundRect.setAttribute("y", "874");
        backgroundRect.setAttribute("width", "144");
        backgroundRect.setAttribute("height", "80");
        backgroundRect.setAttribute("rx", "40");
        backgroundRect.setAttribute("fill", ModColor.fromString(mod.getAcronym()).getDetailedPrimaryColor().toString());


        Element value = doc.createElementNS(namespaceSVG, "text");
        value.setAttribute("class", "cls-1");
        value.setAttribute("font-weight", "600");
        value.setAttribute("font-size", "65");
        value.setAttribute("text-anchor", "middle");
        value.setAttribute("x", "1743");
        value.setAttribute("y", "936");
        value.setAttribute("fill", "#f3f3f3");
        value.setTextContent(mod.getAcronym());

        group.appendChild(backgroundRect);
        group.appendChild(value);
        svgRoot.appendChild(group);
    }


    @Getter
    public enum ThumbnailClassicalAttribute
    {
        HP(95,220,"#4CA6FF"),
        CS(95,220,"#80D926"),
        AR(95,220,"#FF4C4C"),
        OD(95,220,"#BD660F"),
        BPM(140,280,"#C44DFF"),
        LENGTH(180,350,"#C44DFF");
       private int widthOfLeft;
       private int offset;
       private String hex;
       ThumbnailClassicalAttribute(int widthOfLeft,int offset, String hex)
       {
           this.widthOfLeft=widthOfLeft;
           this.offset = offset;
           this.hex= hex;
       }
        public static ThumbnailClassicalAttribute fromName(String name) {
            for (ThumbnailClassicalAttribute attr : values()) {
                if (attr.name().toLowerCase().equals(name.trim())) {
                    return attr;
                }
            }
            return null;
        }
        public static List<ThumbnailClassicalAttribute> parseAttribute(String input)
        {
            if (input==null || input.isEmpty())
                return null;
            List<String> attrStrings = Arrays.stream(input.split(" "))
                    .distinct()
                    .limit(6)
                    .toList();
            List<ThumbnailClassicalAttribute> result = new ArrayList<>();
            for (String attrString : attrStrings) {
                ThumbnailClassicalAttribute attr = fromName(attrString);
                if (attr != null) {
                    result.add(attr);
                }
            }
            return result;
        }
    }


}
