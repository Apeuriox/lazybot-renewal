package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapStatisticsParameter extends LazybotCommandParameter
{
    private String modCombination;
    private Integer beatmapId;
    private Double targetAccuracy;
    private Double approachRate;
    private Double circleSize;
    private Double overallDifficulty;

    public BeatmapStatisticsParameter(String modCombination, Integer beatmapId, String mode, Integer version, String playerName)
    {
        this.modCombination=modCombination;
        this.setMode(mode);
        this.beatmapId=beatmapId;
        this.setPlayerName(playerName);
    }

    @Override
    public void validateParams() {
        if(beatmapId==null) {
            throw new IllegalArgumentException("bid输入值为空或存在不合法参数");
        }
        if(beatmapId<=0) {
            throw new IllegalArgumentException("bid输入值不合法: " + beatmapId);
        }
        if(modCombination!=null && modCombination.length()%2!=0) {
            throw new IllegalArgumentException("mod输入值不合法: " + modCombination);
        }
        if(targetAccuracy==null || (targetAccuracy<0 || targetAccuracy>100)) {
            targetAccuracy=100.0;
        }
    }
    public static BeatmapStatisticsParameter analyzeParameter(List<String> params)
    {
        BeatmapStatisticsParameter result = new BeatmapStatisticsParameter();

        // Normalize split suffix values ("AR 9.5") into their compact form ("AR9.5").
        List<String> processed = new ArrayList<>(
                params == null ? List.of() : params);
        for (int i = processed.size() - 2; i >= 0; i--) {
            if (Parsers.DIFFICULTY_OVERRIDE_PREFIX.matcher(processed.get(i)).matches()
                    && Parsers.NUMBER.matcher(processed.get(i + 1)).matches()) {
                processed.set(i, processed.get(i) + processed.get(i + 1));
                processed.remove(i + 1);
            }
        }

        ArgumentParser p = ArgumentParser.of(processed);

        // Difficulty overrides form a suffix and may appear in any order.
        Set<String> overriddenAttributes = new HashSet<>();
        p.tryPopAll(Parsers.DIFFICULTY_OVERRIDE, m -> {
            String attribute = m.group(1).toUpperCase(Locale.ROOT);
            if (!overriddenAttributes.add(attribute)) {
                throw new IllegalArgumentException(attribute + "覆写值不能重复");
            }
            setDifficultyOverride(result, attribute, Double.parseDouble(m.group(2)));
        });

        p.tryPopIf(Parsers.NUMBER,
                m -> Double.parseDouble(m.group()) <= 100,
                m -> result.setTargetAccuracy(Double.parseDouble(m.group())));

        p.tryPop(Parsers.BID_PLUS_MOD_NO_SPACE, m -> {
            result.setBeatmapId(Integer.parseInt(m.group(1)));
            String modVal = m.group(2);
            if (modVal.length() % 2 != 0) {
                throw new IllegalArgumentException("不合法的Mods组合: " + modVal);
            }
            result.setModCombination(modVal);
        });

        if (result.getModCombination() == null) {
            p.tryPop(Parsers.MOD, m -> {
                String modStr = m.group(1);
                if (modStr.length() % 2 != 0) {
                    throw new IllegalArgumentException("不合法的Mods组合: " + modStr);
                }
                result.setModCombination(modStr);
            });
        }

        if (result.getBeatmapId() == null) {
            p.tryPop(Parsers.DIGITS, m -> result.setBeatmapId(Integer.parseInt(m.group())));
        }

        String remainder = p.remainder();
        if (remainder.length() > 1) {
            result.setPlayerName(remainder);
        }
        return result;
    }

    private static void setDifficultyOverride(
            BeatmapStatisticsParameter result, String attribute, double value)
    {
        switch (attribute) {
            case "AR" -> {
                requireRange("AR", value, 0, 11);
                result.setApproachRate(value);
            }
            case "CS" -> {
                requireRange("CS", value, 0, 10);
                result.setCircleSize(value);
            }
            case "OD" -> {
                requireRange("OD", value, 0, 11);
                result.setOverallDifficulty(value);
            }
            default -> throw new IllegalArgumentException("不支持的难度覆写参数: " + attribute);
        }
    }

    private static void requireRange(String attribute, double value, double minimum, double maximum)
    {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    attribute + "值必须在" + (int) minimum + "-" + (int) maximum + "之间");
        }
    }

    public static void setupDefaultValue(BeatmapStatisticsParameter scoreParameter, UserBindingPO accessTokenPO)
    {
        scoreParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(accessTokenPO.getDefault_mode());
    }

}
