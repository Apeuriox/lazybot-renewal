package me.aloic.lazybot.util;

import de.androidpit.colorthief.ColorThief;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreDTO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
            throw new RuntimeException("parseIntError, s:" + s );
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
                throw new LazybotRuntimeException("Invalid mod combination: "+ modCombination);
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
            throw new LazybotRuntimeException("Incorrect timestamp format: "+ timestamp);
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

    public static int[] getDominantColorColorThief(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        return ColorThief.getColor(image);
    }
    public static Integer getDominantHueColorThief(File imageFile) throws IOException {
        return rgbToHue(getDominantColorColorThief(imageFile));
    }

    public static String rgbToHsl(int[] rgb) {
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
    public static List<Double> rgbToHslDetailed(int[] rgb) {
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
        return List.of(h,s,l);
    }

    private static Integer rgbToHue(double r, double g, double b) {
        r /= 255.0;
        g /= 255.0;
        b /= 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double delta = max - min;
        double l = (max + min) / 2;
        if (l<0.05) return 361;
        if (l>0.92) return 361;
        double s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
        }
        if (s<0.07) return 361;
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
    public static String formatNumber(int number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fk", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }

    public static int[] hexToRgb(String hex) {
        if (hex.length() != 6) {
            throw new IllegalArgumentException("HEX颜色必须是6位字符");
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[] { r, g, b };
    }

    public static Integer rgbToHue(int[] rgb) {
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
        if (l<0.05) return 361;
        if (l>0.92) return 361;
        double s = 0;
        if (delta != 0) {
            s = delta / (1 - Math.abs(2 * l - 1));
        }
        if (s<0.07) return 361;

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

    public static Boolean isPositiveInteger(String num)
    {
        return num.matches("[1-9][0-9]*");
    }

    public static String calculateMD5(File file)
    {
        if (!file.getName().endsWith(".osu")) {
            throw new IllegalArgumentException("校验和计算: 不支持的文件类型");
        }
        try (FileInputStream fis = new FileInputStream(file);
             FileChannel channel = fis.getChannel()) {
             MessageDigest digest = MessageDigest.getInstance("MD5");
             ByteBuffer buffer = ByteBuffer.allocateDirect(512);

            while (channel.read(buffer) != -1) {
                buffer.flip();
                digest.update(buffer);
                buffer.clear();
            }
            byte[] md5Bytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : md5Bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("计算MD5时出错: " + e.getMessage());
        }
    }


    public static String abbrNumber(Integer number) {
        if (number < 1000) return String.valueOf(number);
        if (number < 1000000) return String.format("%.1fK", number / 1000.0);
        if (number < 1000000000) return String.format("%.1fM", number / 1000000.0);
        return String.format("%.1fB", number / 1000000000.0);
    }
    public static int randomNumberGenerator(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    public static void cropAndResize(String pathToFile, int targetWidth, int targetHeight) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(pathToFile));
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            if(originalWidth== targetWidth && originalHeight == targetHeight) {
                return;
            }
            double aspectRatio = (double) originalWidth / originalHeight;
            double targetRatio = (double) targetWidth / targetHeight;

            int scaledWidth, scaledHeight;
            if (aspectRatio > targetRatio) {
                scaledHeight = targetHeight;
                scaledWidth = (int) (targetHeight * aspectRatio);
            } else {
                scaledWidth = targetWidth;
                scaledHeight = (int) (targetWidth / aspectRatio);
            }

            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            BufferedImage scaledBufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaledBufferedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
            int cropX = (scaledWidth - targetWidth) / 2;
            int cropY = (scaledHeight - targetHeight) / 2;
            BufferedImage croppedImage = scaledBufferedImage.getSubimage(cropX, cropY, targetWidth, targetHeight);
            ImageIO.write(croppedImage, "jpg", new File(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
            throw new LazybotRuntimeException("图像缩放时出错: " + e);
        }
    }

    public static double lengthBonusCalc(Integer count)
    {
        return 0.95 + 0.4 * Math.min(1.0, count / 2000.0) + (count > 2000 ? Math.log10(count / 2000.0) * 0.5 : 0.0);
    }

    public static double getScaledRatio(double value, double maxValue, double alpha) {
        if (maxValue <= 0 || alpha <= 0) {
            throw new IllegalArgumentException("maxValue must be > 0, and alpha must be > 0.");
        }
        if (value < 0) value = 0;
        if (value > maxValue) value = maxValue;

        double scaledValue = Math.pow(value, alpha);
        double scaledMax = Math.pow(maxValue, alpha);

        return scaledValue / scaledMax;
    }
    public static Integer circularHueSubtract(Integer hue, Integer subtract)
    {
        return (hue-subtract+360)%360;
    }
    public static boolean isWarmColor(int hue) {
        return ((hue >= 270 && hue <= 360) || (hue >= 0 && hue <= 60));
    }

}
