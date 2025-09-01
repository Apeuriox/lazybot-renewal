package me.aloic.lazybot.osu.filter;


import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.filter.score.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterParser {
    private static final Pattern EXPR_PATTERN = Pattern.compile("(\\w+)(>=|<=|==|!=|>|<|~|=|^=|\\$=)(.+)");

    public static ScoreFilter parse(String expr) {
        Matcher m = EXPR_PATTERN.matcher(expr.trim());
        if (!m.matches()) {
            throw new IllegalArgumentException("[Lazybot] 非法的表达式: " + expr + " 请检查输入符号的全半角，仅支持半角符号");
        }

        String field = m.group(1).trim();
        FilterOperatorEnum op = FilterOperatorEnum.fromSymbol(m.group(2).trim());
        String value = m.group(3).trim();

        return switch (field.toLowerCase()) {
            case "mods", "mod", "m" -> new ModsFilter(value, op);
            case "accuracy", "acc" -> new AccuracyFilter(Double.parseDouble(value), op);
            case "combo" -> new ComboFilter(Integer.parseInt(value), op);
            case "bpm" -> new BPMFilter(Double.parseDouble(value), op);
            case "title", "name" -> new TitleFilter(value, op);
            case "rank" -> new RankFilter(value, op);
            case "mapper", "creator" -> new CreatorFilter(value, op);
            case "artist" -> new ArtistFilter(value, op);
            case "version", "difficulty", "diff" -> new VersionFilter(value, op);
            case "ar" -> new ApproachingRateFilter(Double.parseDouble(value), op);
            case "od" -> new OverallDifficultyFilter(Double.parseDouble(value), op);
            case "hp" -> new HealthFilter(Double.parseDouble(value), op);
            case "cs" -> new CircleSizeFilter(Double.parseDouble(value), op);
            case "circle" -> new CircleCountFilter(Integer.parseInt(value), op);
            case "slider" -> new CircleSliderFilter(Integer.parseInt(value), op);
            case "spinner" -> new CircleSpinnerFilter(Integer.parseInt(value), op);
            case "length" -> new LengthFilter(Integer.parseInt(value), op);
            case "great","300" -> new GreatFilter(Integer.parseInt(value), op);
            case "ok", "100" -> new OKFilter(Integer.parseInt(value), op);
            case "meh", "50" -> new MehFilter(Integer.parseInt(value), op);
            case "miss", "0" -> new MissFilter(Integer.parseInt(value), op);
//            case "maxcombo" -> new MaxComboFilter(Integer.parseInt(value), op);
            case "pp", "performance" -> new PerformanceFilter(Double.parseDouble(value), op);
            case "star", "s" -> new StarFilter(Double.parseDouble(value), op);
            default -> throw new IllegalArgumentException("[Lazybot] 未知字段: " + field);
        };
    }
}
