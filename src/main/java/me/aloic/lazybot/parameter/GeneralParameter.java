package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.utils.OsuToolsUtil;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.DataObjectExtractor;

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
        if (parameter.getPlayerName() == null) parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }

    public static GeneralParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.setAccessToken(tokenPO.getAccess_token());
        if (params.getPlayerName()==null) params.setInfoDTO(DataObjectExtractor.extractPlayerInfo(tokenPO.getAccess_token(),params.getPlayerId(),params.getMode()));
        else params.setInfoDTO(DataObjectExtractor.extractPlayerInfo(tokenPO.getAccess_token(),params.getPlayerName(),params.getMode()));
        return params;
    }

}
