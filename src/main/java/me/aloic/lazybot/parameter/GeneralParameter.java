package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ArgumentParser;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor

public class GeneralParameter extends LazybotCommandParameter
{
    private Integer version;
    @Override
    public void validateParams() {

    }
    public GeneralParameter() {
        this.setVersion(0);
    }
    public GeneralParameter(String playerName, String mode)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.setVersion(0);
    }
    public static GeneralParameter analyzeParameter(List<String> params)
    {
        GeneralParameter parameter=new GeneralParameter();
        ArgumentParser parser = ArgumentParser.of(params);
        if (!parser.remainder().isEmpty()) {
            parameter.setPlayerName(parser.remainder());
        }
        return parameter;
    }
    public static void setupDefaultValue(GeneralParameter parameter, UserBindingPO accessTokenPO)
    {
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }

    public static GeneralParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        GeneralParameter params=GeneralParameter.analyzeParameter(event.getCommandParameters());
        params.setVersion(event.getScorePanelVersion());
        GeneralParameter.setupDefaultValue(params,tokenPO);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        return params;
    }

}
