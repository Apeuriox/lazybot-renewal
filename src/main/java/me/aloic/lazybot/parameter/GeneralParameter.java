package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GeneralParameter extends LazybotCommandParameter
{
    @Override
    public void validateParams()
    {

    }
    public GeneralParameter(String playerName, String mode)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
    }
    public static GeneralParameter analyzeParameter(List<String> params)
    {
        GeneralParameter parameter=new GeneralParameter();
        if (!params.isEmpty()) {
            parameter.setPlayerName(String.join(" ", params));
        }
        return parameter;
    }
    public static void setupDefaultValue(GeneralParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }

}
