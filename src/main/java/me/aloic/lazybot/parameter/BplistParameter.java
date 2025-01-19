package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BplistParameter extends LazybotCommandParameter
{
    private Integer from;
    private Integer to;

    public BplistParameter(String playerName, String mode, Integer from, Integer to)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.from=from;
        this.to=to;
    }

    @Override
    public void validateParams()
    {
        if(from>100||to>100) {
            throw new IllegalArgumentException("{FROM} and {TO} must be less than 100");
        }
        if (from>=to){
            throw new IllegalArgumentException("{FROM} must be less than {TO}");
        }
        if (from<=0) {
            throw new IllegalArgumentException("{FROM} and {TO} must be greater than 0");
        }
    }
    public static BplistParameter analyzeParameter(List<String> params)
    {
        BplistParameter parameter=new BplistParameter();
        if (params == null) {
            throw new RuntimeException("请输入范围，例/bplist 1-100");
        }
        else {
            if (params.size() > 1)
                throw new RuntimeException("参数错误，暂不支持查询他人");
            else if (params.size() == 1) {
                try {
                    String[] fromAndTo = params.getFirst().split("-");
                    if (fromAndTo.length != 2)
                        throw new RuntimeException("请输入正确的范围: "+ params.getFirst());
                    int offset=Integer.parseInt(fromAndTo[0]);
                    int endsAt=Integer.parseInt(fromAndTo[1]);
                    int totalCount = endsAt-offset+1;
                    if (offset <= 0 || totalCount <= 0 ||offset > 99 || endsAt>100) {
                        throw new RuntimeException("请输入正确的范围");
                    }
                    parameter.setFrom(offset);
                    parameter.setTo(endsAt);
                }
                catch (Exception e) {
                    throw new RuntimeException("参数错误: " +e.getMessage());
                }
            }
            else
                throw new RuntimeException("乐，哥们输错了");
        }
        return parameter;
    }
    public static void setupDefaultValue(BplistParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());

    }


}
