package me.aloic.lazybot.util;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class FormatHelper
{

    public static String formatDouble(double value, int fractionDigits) {
        return String.format("%." + fractionDigits + "f", value);
    }
    public static String formatDoubleDecimal(double value, int fractionDigits) {
        DecimalFormat df = new DecimalFormat("0." + "0".repeat(Math.max(0, fractionDigits)));
        return df.format(value);
    }
    public static String formatDoubleDecimal(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
    public static String transformNumber(long number)
    {
        return transformNumber(String.valueOf(number));
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
    public static String formatJsonDateToString(String timeStampString, String outputFormat)
    {
        OffsetDateTime odt = OffsetDateTime.parse(timeStampString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        return odt.toLocalDateTime().plusHours(8).format(outputFormatter);
    }
    public static String formatJsonDateToStringNoTimezone(String timeStampString, String outputFormat)
    {
        OffsetDateTime odt = OffsetDateTime.parse(timeStampString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        return odt.toLocalDateTime().plusHours(8).format(outputFormatter);
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
    public static String formatNumberWithMaxLimit(int number, int length) {
        String numStr = String.valueOf(number);

        if (numStr.length() > length) {
            return "9".repeat(length);
        }

        return String.format("%0" + length + "d", number);
    }

    /**
     * Returns the number of leading padding zeros that formatNumberWithMaxLimit
     * would add to {@code value} when formatted to {@code maxDigits} characters.
     * <p>
     * Examples (maxDigits=4): 12 → 2,  123 → 1,  0 → 3,  9999 → 0,  12345 → 0.
     */
    public static int paddingCount(int value, int maxDigits) {
        if (value >= Math.pow(10, maxDigits)) {
            return 0; // clamped to all 9s, no padding
        }
        if (value == 0) {
            return maxDigits - 1;
        }
        return Math.max(0, maxDigits - (int) (Math.log10(value) + 1));
    }

    /** Format seconds to hours string (e.g., 3600 → "1h") */
    public static String formatSecondsToHours(int seconds) {
        return String.valueOf(seconds / 3600);
    }

    /** Get scaled ratio: min(1, value / (limit * scaleFactor)) */
    public static double getScaledRatio(Double value, double limit, double scaleFactor) {
        if (value == null) return 0;
        return Math.min(1.0, value / (limit * scaleFactor));
    }


}
