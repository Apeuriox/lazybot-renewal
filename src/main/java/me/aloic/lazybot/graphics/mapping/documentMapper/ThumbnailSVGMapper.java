package me.aloic.lazybot.graphics.mapping.documentMapper;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.entity.vo.ThumbnailClassicalVO;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;

import java.util.*;

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
        //AERA AMD TEAM PENDING
        if (data.getScore().getPp()>9999)
            data.getScore().setPp(9999D);
        doc.getElementById("pp").setTextContent(Math.round(data.getScore().getPp())+"PP");
        doc.getElementById("ranking").setTextContent("#"+Optional.ofNullable(data.getPosition()).orElse("-"));
        doc.getElementById("accuracy").setTextContent(CommonTool.toString(data.getScore().getAccuracy())+"%");
        doc.getElementById("combo").setTextContent(CommonTool.toString(data.getScore().getMaxCombo())+"X");
        doc.getElementById("pp-s").setTextContent(Math.round(data.getScore().getPp())+"PP");
        doc.getElementById("ranking-s").setTextContent("#"+Optional.ofNullable(data.getPosition()).orElse("-"));
        doc.getElementById("accuracy-s").setTextContent(CommonTool.toString(data.getScore().getAccuracy())+"%");
        doc.getElementById("combo-s").setTextContent(CommonTool.toString(data.getScore().getMaxCombo())+"X");
        setUpMissMessage(doc,data);

        //attr


        String diffColor = "#" + CommonTool.calcDiffColor(data.getScore().getBeatmap().getDifficult_rating());
        String versionColor = "#1c1719";
        if (data.getScore().getBeatmap().getDifficult_rating() < 7.0)
        {
            if (data.getScore().getBeatmap().getDifficult_rating() % 1.0 > 0.5)
                versionColor = "#fed867";
        }
        else
            versionColor = "#fed867";
        doc.getElementById("title").setTextContent(data.getScore().getBeatmap().getTitle());

        doc.getElementById("star-bg").setAttribute("fill", diffColor);
        doc.getElementById("version-dot").setAttribute("fill", versionColor);
        doc.getElementById("version").setAttribute("fill", versionColor);
        doc.getElementById("star").setAttribute("fill", versionColor);
        doc.getElementById("star-bg").setAttribute("fill", versionColor);
        doc.getElementById("starrating").setAttribute("fill", versionColor);

        //mods


        doc.getElementById("comment").setTextContent(data.getComment());

        return doc;
    }
    private static void setUpMissMessage(Document doc, ThumbnailClassicalVO data)
    {
        if (data.getScore().getStatistics().getMiss()>1 && data.getScore().getStatistics().getMiss()<100)
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
                doc.getElementById("miss").setTextContent("S RANK");
                doc.getElementById("miss").setTextContent("S RANK");
                doc.getElementById("miss").setAttribute("font-size","100");
                doc.getElementById("miss-s").setAttribute("font-size","100");
                doc.getElementById("miss").setAttribute("fill", "#abb0c0");
            }
        }
    }
    private enum ThumbnailClassicalAttribute
    {
        HP(0),
        CS(0),
        AR(0),
        OD(0),
        BPM(1),
        LENGTH(2);
       private int value;
       ThumbnailClassicalAttribute(int value)
       {
           this.value = value;
       }
    }


}
