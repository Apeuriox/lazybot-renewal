package me.aloic.lazybot;

import me.aloic.lazybot.osu.utils.SvgUtil;
import me.aloic.lazybot.shiro.utils.MessageEventFactory;
import me.aloic.lazybot.util.CommonTool;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
public class StaticFunctionTest
{
    @Test
    public void testOc()
    {
        String input = "unlink ";
        System.out.println(formatCommand(input));
    }
    private static String formatCommand(String s)
    {
        s = s.replace("！","!").trim();
        s = s.replace("：",":");
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i++) {
            if((sb.charAt(i) == ':' && sb.charAt(i - 1) != ' ')||(sb.charAt(i) == '&' && sb.charAt(i - 1) != ' ')){
                sb.insert(i, ' ');
                i++;
            }
            else if(sb.charAt(i) == ' ' && sb.charAt(i + 1) ==' '){
                sb.deleteCharAt(i);
                i--;
            }
        }
        return sb.toString().trim().toLowerCase();
    }

    @Test
    public void testMatching()
    {
        String s1 = "I'M A BELIEVER";
        String s2 = "i'm a believer";
        System.out.println(isFuzzyMatch(s1, s2,0.6));
        JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
        double score = jws.apply(s1, s2);
        System.out.println(score);
    }

    public static boolean isFuzzyMatch(String original, String input, double thresholdRatio) {
        if (original == null || input == null) return false;
        String cleanOriginal = original.toLowerCase().replaceAll("\\s+", "");
        String cleanInput = input.toLowerCase().replaceAll("\\s+", "");
        int matchLength = longestCommonSubsequence(cleanOriginal, cleanInput);
        int threshold = (int) Math.ceil(cleanOriginal.length() * thresholdRatio);
        return matchLength >= threshold;
    }

    public static int longestCommonSubsequence(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[a.length()][b.length()];
    }

}
