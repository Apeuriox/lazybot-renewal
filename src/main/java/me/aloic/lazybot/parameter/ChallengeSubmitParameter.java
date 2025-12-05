package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.exception.LazybotRuntimeException;
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
    private Integer lazybotId;
    @Override
    public void validateParams() {
        if (beatmapId == null || challengeId == null) throw new LazybotRuntimeException("参数接收不完整");
        if (beatmapId <=0 || challengeId <=0) throw new LazybotRuntimeException("输入参数不合法");
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
            try{
                parameter.setBeatmapId(Integer.parseInt(params.get(1)));
                parameter.setChallengeId(Integer.parseInt(params.getFirst()));
            }
            catch (Exception e){
                throw new LazybotRuntimeException("发现无法解析的参数，请检查输入" );
            }
        }
        return parameter;
    }
    public static void setupDefaultValue(ChallengeSubmitParameter parameter, AccessTokenPO accessTokenPO)
    {

        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
        parameter.setLazybotId(accessTokenPO.getId());
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
