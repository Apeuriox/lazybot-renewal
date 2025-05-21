package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopScoresParameter extends LazybotCommandParameter
{
    private OsuMode ruleset;
    private Integer from;
    private Integer to;
    private Integer limit;

    public TopScoresParameter(String mode,Integer limit)
    {
        this.limit=limit;
        this.ruleset = OsuMode.getMode(mode);
    }

    @Override
    public void validateParams()
    {
        if(limit<=0) {
            throw new LazybotRuntimeException("{Limit} must be greater than 0");
        }
        if (limit > 50) throw new LazybotRuntimeException("最多支持查询50位");
    }

    public static TopScoresParameter analyzeParameter(List<String> params)
    {
        TopScoresParameter parameter=new TopScoresParameter();
        if(params !=null && !params.isEmpty()) {
            if (CommonTool.isPositiveInteger(params.getFirst())) parameter.setLimit(Integer.parseInt(params.getFirst()));
            else throw new LazybotRuntimeException("此参数不为数字: " + params.getFirst());
        }
        return parameter;
    }
    public static void setupDefaultValue(TopScoresParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getMode() == null)
            parameter.setRuleset(OsuMode.getMode(accessTokenPO.getDefault_mode()));
        if (parameter.getLimit() == null)
            parameter.setLimit(10);
    }
}
