package me.aloic.lazybot.graphics.mapping;

import org.w3c.dom.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SVGElementHelper
{
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
}
