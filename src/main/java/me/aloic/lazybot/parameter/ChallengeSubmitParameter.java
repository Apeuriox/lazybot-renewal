package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor

public class ChallengeSubmitParameter extends LazybotCommandParameter
{
    private Integer version;
    private Integer beatmapId;
    private Integer challengeId;
    @Override
    public void validateParams() {

    }
    public ChallengeSubmitParameter() {
        this.setVersion(0);
    }
    public ChallengeSubmitParameter(String playerName, String mode)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.setVersion(0);
    }
    public static ChallengeSubmitParameter analyzeParameter(List<String> params)
    {
        ChallengeSubmitParameter parameter=new ChallengeSubmitParameter();
        if (!params.isEmpty()) {
            parameter.setPlayerName(String.join(" ", params));
        }
        return parameter;
    }
    public static void setupDefaultValue(ChallengeSubmitParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }

    public static ChallengeSubmitParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        ChallengeSubmitParameter params= ChallengeSubmitParameter.analyzeParameter(event.getCommandParameters());
        params.setVersion(event.getScorePanelVersion());
        ChallengeSubmitParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        return params;
    }

}
