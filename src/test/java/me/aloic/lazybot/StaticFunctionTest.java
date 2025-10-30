package me.aloic.lazybot;

import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.utils.ModCalculatorUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class StaticFunctionTest
{
//    @Test
//    public void testOc()
//    {
//        String input = "unlink ";
//        System.out.println(formatCommand(input));
//    }
//    private static String formatCommand(String s)
//    {
//        s = s.replace("！","!").trim();
//        s = s.replace("：",":");
//        StringBuffer sb = new StringBuffer(s);
//        for (int i = 0; i < sb.length(); i++) {
//            if((sb.charAt(i) == ':' && sb.charAt(i - 1) != ' ')||(sb.charAt(i) == '&' && sb.charAt(i - 1) != ' ')){
//                sb.insert(i, ' ');
//                i++;
//            }
//            else if(sb.charAt(i) == ' ' && sb.charAt(i + 1) ==' '){
//                sb.deleteCharAt(i);
//                i--;
//            }
//        }
//        return sb.toString().trim().toLowerCase();
//    }
//
//    @Test
//    public void testMatching()
//    {
//        String s1 = "I'M A BELIEVER";
//        String s2 = "i'm a believer";
//        System.out.println(isFuzzyMatch(s1, s2,0.6));
//        JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
//        double score = jws.apply(s1, s2);
//        System.out.println(score);
//    }
//
//    public static boolean isFuzzyMatch(String original, String input, double thresholdRatio) {
//        if (original == null || input == null) return false;
//        String cleanOriginal = original.toLowerCase().replaceAll("\\s+", "");
//        String cleanInput = input.toLowerCase().replaceAll("\\s+", "");
//        int matchLength = longestCommonSubsequence(cleanOriginal, cleanInput);
//        int threshold = (int) Math.ceil(cleanOriginal.length() * thresholdRatio);
//        return matchLength >= threshold;
//    }
//
//    public static int longestCommonSubsequence(String a, String b) {
//        int[][] dp = new int[a.length() + 1][b.length() + 1];
//
//        for (int i = 1; i <= a.length(); i++) {
//            for (int j = 1; j <= b.length(); j++) {
//                if (a.charAt(i - 1) == b.charAt(j - 1)) {
//                    dp[i][j] = dp[i - 1][j - 1] + 1;
//                } else {
//                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
//                }
//            }
//        }
//        return dp[a.length()][b.length()];
//    }
//    @Test
//    public void filterTest() throws IOException
//    {
//
//        saveImageToLocal(
//                toByteArray(
//                        ImageFilterUtil.applyGameboyFilter(
//                                ImageIO.read(new File("X:\\Lazybot_working_dir\\osuFiles\\playerAvatar\\3972977.jpg")),
//                                2),
//                        "jpg"),
//                "X:\\lazybot-output",
//                "image.jpg");
//
//    }
//    @Test
//    public void filterTestCRT() throws IOException
//    {
//
//        saveImageToLocal(
//                toByteArray(
//                        ImageFilterUtil.applySimpleCRT(
//                                ImageIO.read(new File("X:\\Lazybot_working_dir\\osuFiles\\playerAvatar\\3972977.jpg")),
//                                2,2),
//                        "jpg"),
//                "X:\\lazybot-output",
//                "image.jpg");
//
//    }
//@Test
//public void modsFilterTest() throws IOException
//{
//
//    List<Mod> mods=List.of(new Mod("HD",null),new Mod("DT",null));
//    System.out.println(ScoreFilter.modsComparison(FilterOperatorEnum.CT, mods, "DT"));
//
//}

//    private static byte[] toByteArray(BufferedImage image, String format) throws IOException
//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, format, baos);
//        return baos.toByteArray();
//    }
//    public void saveImageToLocal(byte[] imageByteArray,String filePath, String fileName) {
//        try {
//            Path path = Paths.get(filePath, fileName);
//            Files.write(path, imageByteArray);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//    }
//    @Test
//    public void test()
//    {
//        String player = "wux1n".replace("-", "")
//                .replaceAll("\\d", "");
//       System.out.println( player.toLowerCase()
//               .substring(0, 1).toUpperCase()
//               + player.substring(1));
//    }
//    @Test
//    public void modRankabilityTest()
//    {
//        List<Mod> mods = List.of(new Mod("DT",null),new Mod("HD",null));
//        List<Mod> mods2 = List.of(new Mod("DT",new ModSetting(1.01)),new Mod("HD",null));
//
//        System.out.println(RankedMods.checkModsRankability(mods));
//        System.out.println(RankedMods.checkModsRankability(mods2));
//
//    }

//    @Test
//    public void testModStrCompare(){
//        List<Mod> mods = Arrays.asList(new Mod("HD",null), new Mod("HR",null));
//
//        System.out.println(ModCalculatorUtil.compareMods(mods, "HRHD"));   // true
//        System.out.println(ModCalculatorUtil.compareMods(mods, "HD"));     // false
//        System.out.println(ModCalculatorUtil.compareMods(mods, "HDSO"));   // false
//        System.out.println(ModCalculatorUtil.compareMods(mods, "HRHDHD")); // tru
//    }




}
