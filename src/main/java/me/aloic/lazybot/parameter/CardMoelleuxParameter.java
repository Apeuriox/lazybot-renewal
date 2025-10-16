package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;

import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor

public class CardMoelleuxParameter extends LazybotCommandParameter
{
    private Integer version;
    private Integer overrideHue;
    @Override
    public void validateParams() {

    }
    public CardMoelleuxParameter() {
        this.setVersion(0);
    }
    public CardMoelleuxParameter(Integer playerId, Integer hue, Integer version)
    {
        this.setPlayerId(playerId);
        this.overrideHue=hue;
        this.version=version;
        this.setMode("osu");
    }

    public static CardMoelleuxParameter analyzeParameter(List<String> params)
    {
        CardMoelleuxParameter parameter=new CardMoelleuxParameter();
        if (params.isEmpty())
            return parameter;

        Iterator<String> it = params.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.startsWith("hue=")) {
                if (parameter.getOverrideHue() == null) {
                    try {
                        parameter.setOverrideHue(Integer.parseInt(s.substring(4))%360);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("输入色相值不合法，应为0至360");
                    }
                }
                it.remove();
            }
        }
        if (!params.isEmpty())
            parameter.setPlayerName(String.join(" ", params));

        return parameter;
    }
    public static void setupDefaultValue(CardMoelleuxParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }

    public static CardMoelleuxParameter setupParameter(LazybotSlashCommandEvent event, AccessTokenPO tokenPO)
    {
        CardMoelleuxParameter params= CardMoelleuxParameter.analyzeParameter(event.getCommandParameters());
        params.setVersion(event.getScorePanelVersion());
        CardMoelleuxParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        return params;
    }

}
