package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BpvsParameter extends LazybotCommandParameter
{
    private String comparePlayerName;
    public BpvsParameter(String playerName,String mode,String comparePlayerName)
    {
        this.setMode(mode);
        this.setPlayerName(playerName);
        this.comparePlayerName=comparePlayerName;
    }

    @Override
    public void validateParams()
    {
        if(comparePlayerName.equals(this.getPlayerName())) {
            throw new IllegalArgumentException("你不能和你自己比");
        }
    }
    public static BpvsParameter analyzeParameter(List<String> params)
    {
        BpvsParameter parameter=new BpvsParameter();
        if (params != null && !params.isEmpty()) {
            String combinedParas=String.join(" ", params);
            if (combinedParas.contains("#")) {
                String[] names = combinedParas.split("#");
                if (names.length == 2) {
                   parameter.setPlayerName(names[0].trim());
                   parameter.setComparePlayerName(names[1].trim());
                }
                else throw new LazybotRuntimeException("参数处理错误: " + Arrays.toString(names));
            }
            else parameter.setComparePlayerName(String.join(" ", params));
        }
        return parameter;
    }
    public static void setupDefaultValue(BpvsParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }
}
