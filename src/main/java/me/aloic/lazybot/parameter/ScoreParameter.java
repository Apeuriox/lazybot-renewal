package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

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
        if(beatmapId<=0) {
            throw new IllegalArgumentException("[Lazybot] bid输入值不合法: " + beatmapId);
        }
        if(modCombination!=null && modCombination.length()%2!=0) {
            throw new IllegalArgumentException("[Lazybot] mod输入值不合法: " + modCombination);
        }
        if(version==null) {
            version=0;
        }
    }
    public static ScoreParameter analyzeParameter(List<String> params)
    {
        ScoreParameter scoreParameter=new ScoreParameter();
        if (params == null || params.size() > 2)
            throw new LazybotRuntimeException("[Lazybot] 参数输入错误，bid和mod间请不要使用空格");
        //如果参数只有一个，并且包含+，说明是mod+bid的形式
        else if (params.size() == 1) {
            if(params.getFirst().contains("+")) {
                String[] paras = params.getFirst().split("\\+");
                //正则匹配正整数
                if (CommonTool.isPositiveInteger(paras[0])) {
                   scoreParameter.setBeatmapId(Integer.valueOf(paras[0]));
                   scoreParameter.setModCombination(paras[1]);
                }
                else
                    throw new LazybotRuntimeException("参数解析错误, Modded: true, Player: self");
            }
            else {
                if(CommonTool.isPositiveInteger(params.getFirst()))
                    scoreParameter.setBeatmapId(Integer.valueOf(params.getFirst()));
                else
                    throw new LazybotRuntimeException("参数解析错误, Modded: false, Player: self");
            }
        }
        //两个参数，有+代表有mod
        else if (params.size() == 2)
        {
            if(params.get(1).contains("+"))
            {
                scoreParameter.setPlayerName(params.get(0));
                String[] paras = params.get(1).split("\\+");
                if (CommonTool.isPositiveInteger(paras[0])) {
                    scoreParameter.setBeatmapId(Integer.valueOf(paras[0]));
                    scoreParameter.setModCombination(paras[1]);
                }
                else {
                    throw new LazybotRuntimeException("参数解析错误, Modded: false, Player: others");
                }
            }
            else {
                scoreParameter.setPlayerName(params.get(0));
                if (CommonTool.isPositiveInteger(params.get(1))) {
                    scoreParameter.setBeatmapId(Integer.valueOf(params.get(1)));
                }
                else {
                    throw new LazybotRuntimeException("[Lazybot] 参数解析错误, Modded: false, Player: Others");
                }
            }
        }
        //只有一个参数，且没有指定mod，那就看参数是不是正整数，不是就滚犊子
        else
        {
            throw new LazybotRuntimeException("[Lazybot] 未知错误, 可能为参数长度>=3");
        }
        return scoreParameter;
    }
    public static void setupDefaultValue(ScoreParameter scoreParameter, AccessTokenPO accessTokenPO)
    {
        scoreParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(accessTokenPO.getDefault_mode());
        if (scoreParameter.getVersion() == null)
            scoreParameter.setVersion(1);

    }

}
