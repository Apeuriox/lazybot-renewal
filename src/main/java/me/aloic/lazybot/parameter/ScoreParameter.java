package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreParameter extends LazybotCommandParameter
{
    private String modCombination;
    private Integer beatmapId;
    private Integer version;

    private Long channelId;
    public ScoreParameter(String modCombination, Integer beatmapId, String mode, Integer version, String playerName)
    {
        this.modCombination=modCombination;
        this.setMode(mode);
        this.beatmapId=beatmapId;
        this.version=version;
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
        if(version==null) {
            version=0;
        }
    }
    public static ScoreParameter analyzeParameter(List<String> params)
    {
        ScoreParameter result = new ScoreParameter();
        ArgumentParser p = ArgumentParser.of(params);

        p.tryPop(Parsers.ALGORITHM_VERSION,
                matcher -> result.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(matcher.group())));

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
        if (!p.remainder().isEmpty())
            result.setPlayerName(p.remainder());
        return result;
    }
    public static void setupDefaultValue(ScoreParameter scoreParameter, AccessTokenPO accessTokenPO)
    {
        scoreParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(accessTokenPO.getDefault_mode());
        if (scoreParameter.getVersion() == null)
            scoreParameter.setVersion(0);

    }

    @Override
    public String toString()
    {
        return "ScoreParameter{" +
                "modCombination='" + modCombination + '\'' +
                ", beatmapId=" + beatmapId +
                ", version=" + version +
                ", playerName='" + this.getPlayerName() + '\'' +
                '}';
    }
}
