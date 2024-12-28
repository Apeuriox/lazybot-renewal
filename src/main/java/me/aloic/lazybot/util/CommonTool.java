package me.aloic.lazybot.util;

import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonTool {
    public static boolean isEmpty(String s) {
        if(s == null) {
            return true;
        }
        return s.length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isEmpty(Collection collection) {
        if(collection == null) {
            return true;
        }
        return collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static String format(String template, Object... params) {
        if(isEmpty(template)) {
            return template;
        }
        return String.format(template.replace("{}", "%s"), params);
    }

    public static int toInt(String s) {
        int res = 0;
        if(isEmpty(s)) {
            return res;
        }
        try {
            res = Integer.parseInt(s);
        }catch (Exception e) {
            System.out.println("parseIntError, s:" + s );
        }
        return res;
    }

    public static boolean isEmpty(Object obj) {
        if(obj == null) {
            return true;
        }
        return isEmpty(obj.toString());
    }

    public static String toString(Object obj) {
        if(obj == null) {
            return "";
        }
        return obj.toString();

    }

    public static String transformNumber(String number){
        int length = number.length();
        int offset = length%3;
        StringBuilder sb = new StringBuilder(number);
        for(int i = offset; i < sb.length();i += 3){
            sb.insert(i, ',');
            i++;
        }
        if(offset == 0){
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public static String toString(Double num) {
        return toString(num, 2);
    }

    public static String toString(Double num, int pointAft) {
        if(isEmpty(num)) {
            return "";
        }
        return String.format("%" + "." + pointAft + "f", num);
    }

    public static Boolean isLowerCase(char c){
        return c >= 'a' && c <= 'z';
    }

    public static Boolean isUpperCase(char c){
        return c >= 'A' && c <= 'Z';
    }

    public static Boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }


    public static String formatSecondsToHours(long seconds)
    {
        double hours = seconds / 3600.0;
        return String.format("%.2f", hours);
    }
    public static int textWidthRough(String text)
    {
        text=text.toLowerCase();
        int result=0;
        for(int i=0;i<text.length();i++)
        {
            if(text.charAt(i)=='m'||text.charAt(i)=='w')
            {
                result+=3;
            }
            else if (text.charAt(i)=='f'||text.charAt(i)=='i'||text.charAt(i)=='k'||text.charAt(i)=='l'||
                    text.charAt(i)=='r'||text.charAt(i)=='t'||text.charAt(i)==' ')
            {
                result++;
            }
            else
            {
                result+=2;
            }
        }
        return result;
    }
    public static String[] modCombinationToArray(String modCombination)
    {
        if(modCombination!=null)
        {
            try
            {
                String[] result = new String[modCombination.length() / 2];
                for (int i = 0; i < modCombination.length(); i += 2)
                {
                    result[i/2] = modCombination.substring(i, i + 2).toUpperCase();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException("Invalid mod combination: "+ modCombination);
            }
        }
        else
            return null;
    }
    public static String modArrayToString(String[] modArray)
    {
        if(modArray!=null)
        {
            return String.join(" ",modArray);
        }
        else
            return null;
    }
    public static String[] timestampSpilt(String timestamp)
    {
        try
        {
            String[] result = timestamp.split("T");
            result[0] = result[0].replace("-", "/");
            result[1] = result[1].substring(0, result[1].length() - 1);
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Incorrect timestamp format: "+ timestamp);
        }
    }
    public static String formatHitLength(int hitLength)
    {
        String result=String.valueOf(hitLength / 60);
        String second = String.valueOf(hitLength % 60);
        if(second.length()<2)
        {
            second="0"+second;
        }
        return result.concat(":").concat(second);
    }
    public static String[] checkOnlineResources(Document svgDocument) throws IOException {
        ArrayList<String> result=new ArrayList<>();
        String xlinkns="http://www.w3.org/1999/xlink";
        NodeList imageElements = svgDocument.getElementsByTagName("image");
        for (int i = 0; i < imageElements.getLength(); i++)
        {
            Element imageElement = (Element) imageElements.item(i);
            String imageURL = imageElement.getAttributeNS(xlinkns, "href");
            String id = imageElement.getAttribute("id");
            if (imageURL.startsWith("http://") || imageURL.startsWith("https://"))
            {
                URL imageResource = new URL(imageURL);
                HttpURLConnection connection = (HttpURLConnection) imageResource.openConnection();
                connection.setRequestMethod("HEAD");
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK)
                {
                    result.add(id);
                }
            }
        }
        return result.toArray(new String[0]);
    }
    public static boolean isCorruptedLink(String link) throws IOException
    {
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        return responseCode != HttpURLConnection.HTTP_OK;
    }
    public static String calcDiffColor(double star)
    {
        double bottom,top;
        int r0,r1,g0,g1,b0,b1;
        if(star<0.1)
        {
            return "AAAAAA";
        }
        else if(star<1.25)
        {
            r0=66;
            g0=144;
            b0=251;
            r1=79;
            g1=192;
            b1=255;
            bottom=0.1;
            top=1.25;
        }
        else if(star<2)
        {
            r0=79;
            g0=192;
            b0=255;
            r1=79;
            g1=255;
            b1=213;
            bottom=1.25;
            top=2;
        }
        else if(star<2.5)
        {
            r0=79;
            g0=255;
            b0=213;
            r1=124;
            g1=255;
            b1=79;
            bottom=2;
            top=2.5;
        }
        else if(star<3.3)
        {
            r0=124;
            g0=255;
            b0=79;
            r1=246;
            g1=240;
            b1=92;
            bottom=2.5;
            top=3.3;
        }
        else if(star<4.2)
        {
            r0=246;
            g0=240;
            b0=92;
            r1=255;
            g1=128;
            b1=104;
            bottom=3.3;
            top=4.2;
        }
        else if(star<4.9)
        {
            r0=255;
            g0=128;
            b0=104;
            r1=255;
            g1=78;
            b1=111;
            bottom=4.2;
            top=4.9;
        }
        else if(star<5.8)
        {
            r0=255;
            g0=78;
            b0=111;
            r1=198;
            g1=69;
            b1=184;
            bottom=4.9;
            top=5.8;
        }
        else if(star<6.7)
        {
            r0=198;
            g0=69;
            b0=184;
            r1=101;
            g1=99;
            b1=222;
            bottom=5.8;
            top=6.7;
        }
        else if(star<7.7)
        {
            r0=101;
            g0=99;
            b0=222;
            r1=24;
            g1=21;
            b1=142;
            bottom=6.7;
            top=7.7;
        }
        else if(star<9)
        {
            r0=24;
            g0=21;
            b0=142;
            r1=0;
            g1=0;
            b1=0;
            bottom=7.7;
            top=9;
        }
        else{
            return "000000";
        }
        double s=(star-bottom)/(top-bottom);
        String rHex=colorToHex(r0,r1,s,0.9);
        String gHex=colorToHex(g0,g1,s,0.9);
        String bHex=colorToHex(b0,b1,s,0.9);
        return rHex+gHex+bHex;
    }

    public static String colorToHex(double color0,double color1,double s,double gamma)
    {
        String result=String.format("%x", Math.min(Math.max(
                Math.round(Math.pow((1.0-s)*Math.pow(color0,gamma)+s*Math.pow(color1,gamma),1.0/gamma)),0),255));
        if(result.length()==1)
        {
            return "0"+result;
        }
        else
        {
            return result.substring(0,2);
        }
    }
    @Deprecated
    public static String modSubColorGenerator(String initialColor,int type)
    {
        int r=hexToDecimal(initialColor.substring(0,2));
        int g=hexToDecimal(initialColor.substring(2,4));
        int b=hexToDecimal(initialColor.substring(4,6));
        if(type==0)
        {
            r= r+90>255?255: r+20;
            g= g+197>255?255: g+40;
            b= b-31<0?0: b-25;
            return hexFormat(r).concat(hexFormat(g).concat(hexFormat(b)));
        }
        else if(type==1)
        {
            r= r+90>255?255: r+32;
            g= g+197>255?255: g+36;
            b= b-31<0?0: b-13;
            return hexFormat(r).concat(hexFormat(g).concat(hexFormat(b)));
        }
        else
        {
            return null;
        }
    }
    private static int hexToDecimal(String hex) {
        int decimal = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int value = Character.isDigit(c) ? c - '0' : c - 'A' + 10;
            decimal = decimal * 16 + value;
        }
        return decimal;
    }
    private static String hexFormat(int decimal)
    {
        String result=String.format("%x",decimal);
        if(result.length()==1)
        {
            return "0"+result;
        }
        else
        {
            return result.substring(0,2);
        }
    }
    public static boolean saveOnlineResource(String link,String fileName) throws IOException
    {
        try (InputStream inputStream = new URL(link).openStream())
        {
            Files.copy(inputStream, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static Double totalPpCalculator(List<ScoreVO> scoreList)
    {
        return scoreList.stream()
                .mapToDouble(score -> Math.pow(0.95, scoreList.indexOf(score)) * score.getPp())
                .sum();
    }
    public static Double totalPpCalculatorFixed(List<ScoreVO> scoreList)
    {
        return scoreList.stream()
                .mapToDouble(score -> Math.pow(0.95, scoreList.indexOf(score)) * score.getPpDetailsLocal().getIfFc())
                .sum();
    }
    public static Double totalPpCalculatorDTO(List<ScoreDTO> scoreList)
    {
        return scoreList.stream()
                .mapToDouble(score -> Math.pow(0.95, scoreList.indexOf(score)) * score.getPp())
                .sum();
    }
    public static Double totalPpCalculatorList(List<Double> ppList) {
        AtomicInteger index = new AtomicInteger(0);
        return ppList.stream()
                .mapToDouble(pp -> Math.pow(0.95, index.getAndIncrement()) * pp)
                .sum();
    }
    public static Date tranfromDate(int year,int month,int day,int hour,int minute,int second)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }
    public static boolean modsContainsAnyOfStarChanging(String[] array)
    {
        List<String> elements = Arrays.asList("HR","DT","HT","EZ","FL","NC","TD");
        return Arrays.stream(array).anyMatch(elements::contains);
    }

}
