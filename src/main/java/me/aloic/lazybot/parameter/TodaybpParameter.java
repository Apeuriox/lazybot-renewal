package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodaybpParameter extends LazybotCommandParameter
{
    private Integer maxDays;
    public TodaybpParameter(String playerName,String mode,Integer maxDays)
    {
        this.maxDays=maxDays;
        this.setPlayerName(playerName);
        this.setMode(mode);
    }

    @Override
    public void validateParams()
    {
        if(maxDays<=0) {
            throw new IllegalArgumentException("Days must be greater than 0");
        }
        if(maxDays>=10000)
        {
            throw new IllegalArgumentException("Days is longer than total osu game lifespan");
        }
    }
    public static TodaybpParameter analyzeParameter(List<String> params)
    {
        TodaybpParameter parameter=new TodaybpParameter();
        if (params != null && !params.isEmpty()) {
            if (params.size() == 1) {
                if(params.getFirst().contains("#")) {
                    String[] paras = params.getFirst().split("#");
                    if (CommonTool.isPositiveInteger(paras[1]))
                        parameter.setMaxDays(Integer.parseInt(paras[1]));
                    else
                        throw new RuntimeException("参数解析错误, index不为正整数，length=1");
                }
                else if(CommonTool.isPositiveInteger(params.getFirst()))
                    parameter.setMaxDays(Integer.parseInt(params.getFirst()));
                else {
                    parameter.setPlayerName(params.getFirst());
                    parameter.setMaxDays(1);
                }
            }
            else if (params.size() == 2) {
                if(params.get(1).contains("#")) {
                    String[] paras = params.get(1).split("#");
                    parameter.setPlayerName(params.getFirst());
                    if (CommonTool.isPositiveInteger(paras[1]))
                        parameter.setMaxDays(Integer.parseInt(paras[1]));
                    else
                        throw new RuntimeException("参数解析错误, index不为正整数,length=2");
                }
                else if(CommonTool.isPositiveInteger(params.get(1))) {
                    parameter.setPlayerName(params.getFirst());
                    parameter.setMaxDays(Integer.parseInt(params.get(1)));
                }
            }
            else {
                throw new RuntimeException("Incorrect parameters: " + params);
            }
        }
        else {
            parameter.setMaxDays(1);
        }
        return parameter;
    }
    public static void setupDefaultValue(TodaybpParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
        if (parameter.getMaxDays() == null)
            parameter.setMaxDays(1);
    }
}
