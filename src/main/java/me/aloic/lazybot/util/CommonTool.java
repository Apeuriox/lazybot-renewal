package me.aloic.lazybot.util;

import de.androidpit.colorthief.ColorThief;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
    public static String hslFormat(Integer hue, Integer saturation, Integer value)
    {
        return String.format("hsl(%d,%d%%,%d%%)", hue, saturation, value);
    }

    /**
     * 将 HSL 转换为 HEX 颜色表示
     *
     * @param h 色相（Hue），范围 0-360
     * @param s 饱和度（Saturation），范围 0-1
     * @param v 明度（lightness），范围 0-1
     * @return HEX 颜色表示（如 #RRGGBB）
     */
    public static String hsvToHex(float h, float s, float v) {
        int r, g, b;

        float c = v * s; // Chroma
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        if (h < 60) {
            r = Math.round((c + m) * 255);
            g = Math.round((x + m) * 255);
            b = Math.round(m * 255);
        } else if (h < 120) {
            r = Math.round((x + m) * 255);
            g = Math.round((c + m) * 255);
            b = Math.round(m * 255);
        } else if (h < 180) {
            r = Math.round(m * 255);
            g = Math.round((c + m) * 255);
            b = Math.round((x + m) * 255);
        } else if (h < 240) {
            r = Math.round(m * 255);
            g = Math.round((x + m) * 255);
            b = Math.round((c + m) * 255);
        } else if (h < 300) {
            r = Math.round((x + m) * 255);
            g = Math.round(m * 255);
            b = Math.round((c + m) * 255);
        } else {
            r = Math.round((c + m) * 255);
            g = Math.round(m * 255);
            b = Math.round((x + m) * 255);
        }
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static String getAverageColor(File imageFile) throws IOException {
        BufferedImage image = resizeImage(ImageIO.read(imageFile), 100, 100);

        long totalR = 0, totalG = 0, totalB = 0;
        int pixelCount = 0;

        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);

                // 忽略透明部分
                if ((rgb >> 24) == 0x00) continue;

                totalR += (rgb >> 16) & 0xFF;
                totalG += (rgb >> 8) & 0xFF;
                totalB += rgb & 0xFF;
                pixelCount++;
            }
        }

        // 计算平均值
        int avgR = (int) (totalR / pixelCount);
        int avgG = (int) (totalG / pixelCount);
        int avgB = (int) (totalB / pixelCount);

        return String.format("#%02X%02X%02X", avgR, avgG, avgB);
    }
    public static String getDominantHSLWithBins(File imageFile, int binSize) throws IOException {
        int dominantColor =  calcDominantColor(imageFile, binSize);
        return rgbToHsl((dominantColor >> 16) & 0xFF, (dominantColor >> 8) & 0xFF, dominantColor & 0xFF);
    }
    public static Integer getDominantHueWithBins(File imageFile, int binSize) throws IOException {
        int dominantColor =  calcDominantColor(imageFile, binSize);
        return rgbToHue((dominantColor >> 16) & 0xFF, (dominantColor >> 8) & 0xFF, dominantColor & 0xFF);
    }

    private static int calcDominantColor(File imageFile, int binSize) throws IOException
    {
        BufferedImage image = resizeImage(ImageIO.read(imageFile), 100, 100); // 缩小图片到 100x100
        Map<Integer, Integer> colorFrequency = new ConcurrentHashMap<>();
        int width = image.getWidth();
        int height = image.getHeight();
//        IntStream.range(0, width).parallel().forEach(x -> {
//            for (int y = 0; y < height; y++) {
//                int rgb = image.getRGB(x, y);
//
//                if ((rgb >> 24) == 0x00) continue;
//
//                int r = ((rgb >> 16) & 0xFF) / binSize * binSize;
//                int g = ((rgb >> 8) & 0xFF) / binSize * binSize;
//                int b = (rgb & 0xFF) / binSize * binSize;
//
//                int binnedColor = (r << 16) | (g << 8) | b;
//                frequency.incrementAndGet(binnedColor);
//            }
//        });
//
//        // 找到出现频率最高的颜色
//        int dominantColor = 0;
//        int maxFrequency = 0;
//        for (int i = 0; i < frequency.length(); i++) {
//            int freq = frequency.get(i);
//            if (freq > maxFrequency) {
//                maxFrequency = freq;
//                dominantColor = i;
//            }
//        }
//        return dominantColor;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                if ((rgb >> 24) == 0x00) continue;
                int r = ((rgb >> 16) & 0xFF) / binSize * binSize;
                int g = ((rgb >> 8) & 0xFF) / binSize * binSize;
                int b = (rgb & 0xFF) / binSize * binSize;

                int binnedColor = (r << 16) | (g << 8) | b;
                colorFrequency.put(binnedColor, colorFrequency.getOrDefault(binnedColor, 0) + 1);
            }
        }

        return colorFrequency.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
    public static int getDominantColorHue(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        int[] dominantColor =  ColorThief.getColor(image);
        return rgbToHue(dominantColor);
    }
    private static String rgbToHsl(double r, double g, double b) {
        r /= 255.0;
        g /= 255.0;
        b /= 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;

        double l = (max + min) / 2;
        double s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
        }

        double h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * ((g - b) / delta % 6);
            } else if (max == g) {
                h = 60 * ((b - r) / delta + 2);
            } else if (max == b) {
                h = 60 * ((r - g) / delta + 4);
            }
        }
        if (h < 0) {
            h += 360;
        }
        return String.format("hsl(%.0f, %.0f%%, %.0f%%)", h, s * 100, l * 100);
    }
    private static String rgbToHsl(int[] rgb) {
        double r=rgb[0];
        double g=rgb[1];
        double b=rgb[2];
        r/= 255.0;
        g /= 255.0;
        b /= 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;

        double l = (max + min) / 2;
        double s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
        }

        double h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * ((g - b) / delta % 6);
            } else if (max == g) {
                h = 60 * ((b - r) / delta + 2);
            } else if (max == b) {
                h = 60 * ((r - g) / delta + 4);
            }
        }
        if (h < 0) {
            h += 360;
        }
        return String.format("hsl(%.0f, %.0f%%, %.0f%%)", h, s * 100, l * 100);
    }
    private static Integer rgbToHue(double r, double g, double b) {
        r /= 255.0;
        g /= 255.0;
        b /= 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;
        double l = (max + min) / 2;
        if (l<0.04) return 361;
        if (l>0.92) return 361;
        double h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * ((g - b) / delta % 6);
            } else if (max == g) {
                h = 60 * ((b - r) / delta + 2);
            } else if (max == b) {
                h = 60 * ((r - g) / delta + 4);
            }
        }
        if (h < 0) {
            h += 360;
        }
        return (int) h;
    }

    private static Integer rgbToHue(int[] rgb) {
        double r=rgb[0];
        double g=rgb[1];
        double b=rgb[2];
        r/= 255.0;
        g /= 255.0;
        b /= 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;
        double l = (max + min) / 2;
        if (l<0.04) return 361;
        if (l>0.95) return 361;
        double h = 0;
        if (delta != 0) {
            if (max == r) {
                h = 60 * ((g - b) / delta % 6);
            } else if (max == g) {
                h = 60 * ((b - r) / delta + 2);
            } else if (max == b) {
                h = 60 * ((r - g) / delta + 4);
            }
        }
        if (h < 0) {
            h += 360;
        }
        return (int) h;
    }
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        return resizedImage;
    }

}
