package me.aloic.lazybot.graphics.mapping;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.rosupp.AlgorithmVersion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SVGElementHelper
{
    private static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

    public static void mapElementAttributes(Document document, String elementId,String attribute, String value) {
        document.getElementById(elementId).setAttribute(attribute, value);
    }

    public static void mapTextToElement(Document document, String elementId, String value) {
        document.getElementById(elementId).setTextContent(value);
    }

    public static String convertDate(String inputDate) {
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM. d'th', yyyy", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
    public static String convertDateMonth(String inputDate) {
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy. MM", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
    public static String dateNow() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM. d'th', yyyy", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
    public static String dateNowMarathon() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy - MMM. dd", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
    public static String dateNowNumber(){
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return currentDate.format(formatter);
    }

    public static void appendAlgorithmLabel(Document document, AlgorithmVersion algorithm)
    {
        if (document == null || algorithm == null) {
            return;
        }
        Element label = document.createElementNS(SVG_NAMESPACE, "text");
        label.setAttribute("x", "98%");
        label.setAttribute("y", "98%");
        label.setAttribute("text-anchor", "end");
        label.setAttribute("fill", "#ffffff");
        label.setAttribute("fill-opacity", "0.6");
        label.setAttribute("stroke", "#000000");
        label.setAttribute("stroke-opacity", "0.45");
        label.setAttribute("stroke-width", "2");
        label.setAttribute("paint-order", "stroke");
        label.setAttribute("font-size", "12");
        label.setAttribute("font-family", "Arial, sans-serif");
        String text = "PP algo · " + RosuAlgorithmVersionUtil.shortLabel(algorithm);
        if (algorithm != RosuAlgorithmVersionUtil.LATEST) {
            text += " · * selected-algorithm PP";
        }
        label.setTextContent(text);
        document.getDocumentElement().appendChild(label);
    }
}
