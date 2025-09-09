package me.aloic.lazybot.graphics.mapping.documentMapper;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.theme.Color.HSL;
import me.aloic.lazybot.util.CommonTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;

@Slf4j
public class AvatarSVGMapper extends LazybotSVGMapper
{
    public static Document mapPlayerInfoToAvatar(PlayerInfoVO player, int primaryHue, int type)
    {
        Document doc = SVGTemplateLoader.loadSVGTemplate("OsuAvatar");
        int saturationFactor = 1;
        if (primaryHue > 360) saturationFactor = 0;

        doc.getElementById("name").setTextContent(player.getPlayerName());
        if (player.getPlayerName().length()>8) {
            doc.getElementById("name").setAttribute("font-size", String.valueOf(120 - (player.getPlayerName().length()-8)*5));
        }

        doc.getElementById("area").setTextContent(player.getCountryCode());

        if (player.getTeamShortName()!=null)
            doc.getElementById("team").setTextContent(player.getTeamShortName());
        if (player.getPreviousNames()!=null && player.getPreviousNames().length>0)
            doc.getElementById("oldname").setTextContent(String.join(", ", player.getPreviousNames()));
        else doc.getElementById("oldnameall").setAttribute("opacity", "0");

        doc.getElementById("avatar").setAttributeNS(xlinkns, "xlink:href", player.getAvatarUrl());
        if (type==1)
        {
            doc.getElementById("pprect").setAttribute("fill", new HSL(primaryHue, 61*saturationFactor, 74).toString());
            doc.getElementById("pp").setTextContent(CommonTool.toString(Optional.ofNullable(player.getPerformancePoint()).orElse(0.0).toString()));
            doc.getElementById("rank").setTextContent("#".concat(CommonTool.transformNumber(Optional.ofNullable(player.getGlobalRank()).orElse(0).toString())));
        }
        else {
            Element svgRoot = doc.getDocumentElement();
            svgRoot.setAttribute("height", "1025");
            doc.getElementById("lower").setAttribute("opacity", "0");

        }
        return doc;
    }

}
