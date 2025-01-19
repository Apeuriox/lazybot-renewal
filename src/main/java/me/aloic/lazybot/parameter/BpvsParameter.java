package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

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
            throw new IllegalArgumentException("You cannot compare with yourself");
        }
    }
    public static BpvsParameter analyzeParameter(List<String> params)
    {
        BpvsParameter parameter=new BpvsParameter();
        if (params != null && !params.isEmpty()) {
            parameter.setComparePlayerName(String.join(" ", params));
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
