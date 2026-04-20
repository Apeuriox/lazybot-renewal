package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        BeatmapStatisticsParameter result=new BeatmapStatisticsParameter();
        if (!params.isEmpty()) {
            String last = params.getLast();
            if (CommonTool.isDecimal(last)) {
                result.setTargetAccuracy(Double.parseDouble(last));
                params.removeLast();
                if (!params.isEmpty()) {
                    last = params.getLast();
                }
            }
            Matcher m = Pattern.compile("^(\\d{1,10})\\+([a-z]+)$").matcher(last);
            if (m.matches()) {
                int idVal = Integer.parseInt(m.group(1));
                String modVal = m.group(2);
                if (modVal.length() % 2 != 0) {
                    throw new IllegalArgumentException("不合法的Mods组合: " + modVal);
                }
                result.beatmapId = idVal;
                result.modCombination = modVal;
                params.removeLast();
            }
        }
        if (result.modCombination == null && !params.isEmpty()) {
            String maybeMods = params.getLast();
            if (maybeMods.startsWith("+")) {
                String modStr = maybeMods.substring(1);
                if (modStr.length() % 2 != 0) {
                    throw new IllegalArgumentException("不合法的Mods组合: " + modStr);
                }
                result.modCombination = modStr;
                params.removeLast();
            }
        }
        if (result.beatmapId == null && !params.isEmpty()) {
            String maybeId = params.getLast().trim();
            if (maybeId.matches("\\d+")) {
                result.beatmapId = Integer.parseInt(maybeId);
                params.removeLast();
            }
        }
        if (!params.isEmpty()) result.setPlayerName(String.join(" ", params).trim());
        return result;
    }
    public static void setupDefaultValue(BeatmapStatisticsParameter scoreParameter, AccessTokenPO accessTokenPO)
    {
        scoreParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(accessTokenPO.getDefault_mode());
    }

}
