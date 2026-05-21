package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsParameter extends LazybotCommandParameter
{
    private String type;

    @Override
    public void validateParams()
    {
        if (type == null || type.isEmpty() || type.isBlank()) {
            throw new LazybotRuntimeException("""
                    [Lazybot] 请输入二级指令: count / usage / updated
                    /pstats count  — 玩家与谱面统计
                    /pstats updated — 上次批量更新数""");
        }
    }

    public static StatsParameter analyzeParameter(List<String> params)
    {
        StatsParameter parameter = new StatsParameter();
        if (params != null && !params.isEmpty()) {
            parameter.setType(params.getFirst().toLowerCase());
        }
        return parameter;
    }
}
