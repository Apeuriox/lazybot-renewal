package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.entity.vo.CheckInStats;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CardCheckInSVGMapper extends LazybotSVGMapper
{
    public static Document mapCheckinStatsToCard(CheckInStats stats)
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("CardCheckIn");
        LocalDate today = LocalDate.now();

        LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();
        int startIndex = firstDayOfMonth.getDayOfWeek().getValue() - 1;

        LocalDate prevMonth = firstDayOfMonth.minusMonths(1);
        int daysInPrevMonth = prevMonth.lengthOfMonth();
        Element svgRoot = document.getDocumentElement();
        int cellIndex = 0;


        for (int i = startIndex - 1; i >= 0; i--) {
            int dayOfMonth = daysInPrevMonth  - i;
            singleDayCard(document,svgRoot,dayOfMonth, 1, cellIndex++);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            int type = 0;
            if (today.getDayOfMonth() == day) {
                type = 2;
            }
            singleDayCard(document,svgRoot,day, type, cellIndex++);
        }
        int nextMonthDay = 1;
        while (cellIndex < 35) {
            singleDayCard(document,svgRoot,nextMonthDay++, 1, cellIndex++);
        }
        document.getElementById("name").setTextContent(stats.getPlayerName());
        document.getElementById("month").setTextContent(today.format(DateTimeFormatter.ofPattern("MMM. yyyy", Locale.ENGLISH)));
        document.getElementById("lazycoin").setTextContent(String.valueOf(stats.getLazyCoins()));
        document.getElementById("lazycoinDiff").setTextContent(String.format(" (+%s) LazyCoins",stats.getLazyCoinsDiff()));
        document.getElementById("lazycoinTotal").setTextContent(String.valueOf(stats.getTotalLazyCoins()));

        document.getElementById("cumDay").setTextContent(String.valueOf(stats.getTotalCheckIns()));
        document.getElementById("conDay").setTextContent(String.valueOf(stats.getContinuousCheckIns()));
        document.getElementById("avatar").setAttributeNS(xlinkns,"xlink:href", stats.getAvatar_url());


        return document;
    }
    private static void singleDayCard(Document document, Element svgRoot ,int dayOfMonth, int type, int index)
    {
        Node sectionFullNode = document.createElementNS(namespaceSVG, "g");
        Element sectionFull = (Element) sectionFullNode;

        Node singleDayBGNode = document.createElementNS(namespaceSVG, "rect");
        Element singleDayBG = (Element) singleDayBGNode;
        singleDayBG.setAttribute("x", "21");
        singleDayBG.setAttribute("y", "161");
        singleDayBG.setAttribute("width", "170");
        singleDayBG.setAttribute("height", "140");
        singleDayBG.setAttribute("rx", "10");
        singleDayBG.setAttribute("ry", "10");

        Node indexNode = document.createElementNS(namespaceSVG, "text");
        Element indexOfMonth = (Element) indexNode;
        indexOfMonth.setAttribute("class", "cls-1");
        indexOfMonth.setAttribute("text-anchor", "end");
        indexOfMonth.setAttribute("font-size", "35");
        indexOfMonth.setAttribute("x", "178");
        indexOfMonth.setAttribute("y", "200");
        indexOfMonth.setTextContent(String.valueOf(dayOfMonth));

        if (type==0) {
            singleDayBG.setAttribute("fill", "rgb(68,68,68)");
            indexOfMonth.setAttribute("fill", "rgb(200,201,202)");
        }
        else if (type==1) {
            singleDayBG.setAttribute("fill","#27292b");
            indexOfMonth.setAttribute("fill", "rgb(85,85,85)");
        }
        else {
            singleDayBG.setAttribute("fill","#27292b");
            singleDayBG.setAttribute("width","164");
            singleDayBG.setAttribute("height","134");
            singleDayBG.setAttribute("x","24");
            singleDayBG.setAttribute("y","164");
            singleDayBG.setAttribute("stroke","#686A6C");
            singleDayBG.setAttribute("stroke-width","6");
            indexOfMonth.setAttribute("fill", "#C8C9CA");
        }

        sectionFull.appendChild(singleDayBG);
        sectionFull.appendChild(indexOfMonth);
        sectionFull.setAttribute("transform", String.format("translate(%s %s)", (index%7)*178,  (index/7) * 148));
        svgRoot.appendChild(sectionFull);
    }

}
