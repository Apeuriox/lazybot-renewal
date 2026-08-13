package me.aloic.lazybot.osu.utils;

import me.aloic.lazybot.osu.dao.entity.dto.plus.LazybotScoreStatistics;

import java.util.List;

public class GradeCalculator {

    //why did not I just store this shit in database? oh come on
    public static String calculateGrade(LazybotScoreStatistics stats, List<String> mods) {
        boolean isSilver = mods != null && (mods.contains("HD") || mods.contains("FL"));
        stats.reInitialize();
        return calculateGrade(stats.getCount300(),stats.getCount100(),stats.getCount50(),stats.getCount0(),isSilver);
    }
    public static String calculateGrade(int count300,int count100,int count50,int count0 ,boolean isSilver) {
        int totalHits = count300 + count100 + count50 + count0;
        // actually illegal here
        if (totalHits == 0)
        {
            return "D";
        }
        double ratio300 = (double) count300 / totalHits;
        double ratio50 = (double) count50 / totalHits;

        if (ratio300 == 1.0) {
            return isSilver ? "XH" : "X";
        }

        if (ratio300 > 0.9 && ratio50 <= 0.01 && count0 == 0) {
            return isSilver ? "SH" : "S";
        }

        if ((ratio300 > 0.8 && count0 == 0) || ratio300 > 0.9) {
            return "A";
        }

        if ((ratio300 > 0.7 && count0 == 0) || ratio300 > 0.8) {
            return "B";
        }

        if (ratio300 > 0.6) {
            return "C";
        }

        return "D";
    }
}