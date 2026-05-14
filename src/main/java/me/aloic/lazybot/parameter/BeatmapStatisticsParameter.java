package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapStatisticsParameter extends LazybotCommandParameter
{
    private String modCombination;
    private Integer beatmapId;
    private Double targetAccuracy;

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
            throw new IllegalArgumentException("bid输入值为空");
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
        ArgumentParser p = ArgumentParser.of(params);

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
    public static void setupDefaultValue(BeatmapStatisticsParameter scoreParameter, AccessTokenPO accessTokenPO)
    {
        scoreParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(accessTokenPO.getDefault_mode());
    }

}
