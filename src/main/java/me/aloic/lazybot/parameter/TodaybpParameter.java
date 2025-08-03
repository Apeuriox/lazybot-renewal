package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodaybpParameter extends LazybotCommandParameter
{
    private Integer maxDays;
    public TodaybpParameter(String playerName,String mode,Integer maxDays)
    {
        this.maxDays=maxDays;
        this.setPlayerName(playerName);
        this.setMode(mode);
    }

    @Override
    public void validateParams()
    {
        if(maxDays<=0) {
            throw new IllegalArgumentException("[Lazybot] 查询的天数需要为正整数");
        }
        if(maxDays>=10000)
        {
            throw new IllegalArgumentException("[Lazybot] 查询的天数不能大于Osu的整个生命周期");
        }
    }
    public static TodaybpParameter analyzeParameter(List<String> params)
    {
        TodaybpParameter parameter=new TodaybpParameter();
        String text = String.join(" ", params).trim();
        if (text.matches("\\d+")) {
            int indexVal = Integer.parseInt(text);
            if (indexVal >= 1)
                parameter.maxDays = indexVal;
            else
            {
                parameter.setPlayerName(text);
                parameter.maxDays = 1;
            }
        }
        else {
            Matcher indexMatcher = Pattern.compile("#(\\d+)").matcher(text);
            if (indexMatcher.find()) {
                parameter.maxDays = Integer.parseInt(indexMatcher.group(1));
                text = text.replace(indexMatcher.group(), "").trim();
            }
            if (!text.isEmpty()) parameter.setPlayerName(text);
        }
        return parameter;
    }
    public static void setupDefaultValue(TodaybpParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
        if (parameter.getMaxDays() == null)
            parameter.setMaxDays(1);
    }
}
