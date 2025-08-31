package me.aloic.lazybot.parameter;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.filter.FilterParser;
import me.aloic.lazybot.osu.filter.ScoreFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreFilterParameter extends LazybotCommandParameter
{
    private List<ScoreFilter> filters;

    public ScoreFilterParameter(@Nonnull String playerName, @Nonnull String mode) {
        this.setPlayerName(playerName);
        this.setMode(mode);
    }

    @Override
    public void validateParams()
    {
        if (this.getFilters()==null || this.getFilters().isEmpty()) throw new LazybotRuntimeException("[Lazybot] 你这过滤器怎么是空的啊，小妹妹打错字了吧");
    }
    public static ScoreFilterParameter analyzeParameter(List<String> params)
    {
        ScoreFilterParameter parameter=new ScoreFilterParameter();
        if (params == null||params.isEmpty()) throw new LazybotRuntimeException("[Lazybot] 参数呢?");
        else {
            String userInput = String.join(" ", params);
            List<ScoreFilter> filters = Arrays.stream(userInput.split(","))
                    .map(String::trim)
                    .map(FilterParser::parse)
                    .toList();
            parameter.setFilters(filters);
        }
        return parameter;

    }
    public static void setupDefaultValue(ScoreFilterParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
    }
}
