package me.aloic.lazybot.osu.filter;

import me.aloic.lazybot.enums.FilterOperatorEnum;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
public interface ScoreFilter {
    boolean filter(ScoreLazerDTO score);

    static boolean numericComparison(FilterOperatorEnum operator, Double value, double threshold)
    {
        try{
            return switch (operator) {
                case GT -> value > threshold;
                case GTE -> value >= threshold;
                case LT -> value < threshold;
                case LTE -> value <= threshold;
                case EQ,CT -> value == threshold;
                case NE -> value != threshold;
                default -> throw new IllegalArgumentException("[Lazybot] 不合法的运算符类型: " + operator.getSymbol());
            };
        }
        catch (IllegalArgumentException iae) {
            throw iae;
        }
        catch (Exception e){
            return false;
        }
    }

    static boolean numericComparison(FilterOperatorEnum operator, Integer value, int threshold)
    {
        try{
            return switch (operator) {
                case GT -> value > threshold;
                case GTE -> value >= threshold;
                case LT -> value < threshold;
                case LTE -> value <= threshold;
                case EQ,CT -> value == threshold;
                case NE -> value != threshold;
                default -> throw new IllegalArgumentException("[Lazybot] 不合法的运算符类型: " + operator.getSymbol());
            };
        }
        catch (IllegalArgumentException iae) {
            throw iae;
        }
        catch (Exception e){
            return false;
        }
    }

    static boolean stringComparison(FilterOperatorEnum operator, String existing, String target)
    {
        try{
            existing=existing.toLowerCase().trim();
            target=target.toLowerCase().trim();
            return switch (operator) {
                case EQ -> existing.equals(target);
                case SW -> existing.startsWith(target);
                case EW -> existing.endsWith(target);
                case CT -> existing.contains(target);
                case NE -> !existing.contains(target);
                case LIKE -> new JaroWinklerSimilarity().apply(existing, target)>0.7;
                default -> throw new IllegalArgumentException("[Lazybot] 不合法的运算符类型: " + operator.getSymbol());
            };
        }
        catch (IllegalArgumentException iae) {
            throw iae;
        }
        catch (Exception e){
            return false;
        }
    }

    static boolean modsComparison(FilterOperatorEnum operator, List<Mod> mods, String target)
    {
        try{
            if (operator==FilterOperatorEnum.EQ) {
                return mods.stream().anyMatch(m -> m.getAcronym().equalsIgnoreCase(target));
            }
            else if (operator==FilterOperatorEnum.CT) {
                String modsStr = mods.stream()
                        .map(Mod::getAcronym)
                        .sorted()
                        .collect(Collectors.joining());
                String inputStr = Arrays.stream(target.split("(?<=\\G.{2})"))
                        .sorted()
                        .collect(Collectors.joining());
                return modsStr.equalsIgnoreCase(inputStr);
            }
            throw new IllegalArgumentException("[Lazybot] 不合法的运算符类型: " + operator.getSymbol());
        }
        catch (IllegalArgumentException iae) {
            throw iae;
        }
        catch (Exception e){
            return false;
        }
    }
}
