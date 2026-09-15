package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CardInfoParameter extends GeneralParameter
{
    private Integer lookbackDays;

    public CardInfoParameter()
    {
        this.setVersion(0);
    }

    @Override
    public void validateParams()
    {
        if (lookbackDays != null && lookbackDays <= 0) {
            throw new LazybotRuntimeException("暂不支持逆转时空");
        }
    }

    public static CardInfoParameter analyzeParameter(List<String> params)
    {
        CardInfoParameter parameter = new CardInfoParameter();
        ArgumentParser parser = ArgumentParser.of(params);
        parser.tryPop(Parsers.INDEX, matcher -> parameter.setLookbackDays(Integer.parseInt(matcher.group(1))));
        if (!parser.remainder().isEmpty()) {
            parameter.setPlayerName(parser.remainder());
        }
        return parameter;
    }

    public static void setupDefaultValue(CardInfoParameter parameter, UserBindingPO accessTokenPO)
    {
        GeneralParameter.setupDefaultValue(parameter, accessTokenPO);
    }

    public static CardInfoParameter setupParameter(LazybotSlashCommandEvent event, UserBindingPO tokenPO)
    {
        CardInfoParameter params = analyzeParameter(event.getCommandParameters());
        params.setVersion(event.getScorePanelVersion());
        setupDefaultValue(params, tokenPO);
        if (event.getOsuMode() != null) {
            params.setMode(event.getOsuMode().getDescribe());
        }
        params.validateParams();
        return params;
    }
}
