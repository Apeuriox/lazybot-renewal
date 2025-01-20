package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipsParameter extends LazybotCommandParameter
{
    private Integer id;


    @Override
    public void validateParams()
    {

    }

    public static TipsParameter analyzeParameter(List<String> params)
    {
        TipsParameter parameter=new TipsParameter();
        if (!params.isEmpty()) {
            if (params.size() == 1 && CommonTool.isPositiveInteger(params.getFirst())) {
                parameter.setId(Integer.valueOf(params.getFirst()));
            }
            else {
                if(CommonTool.isPositiveInteger(params.getFirst())) parameter.setId(Integer.valueOf(params.getFirst()));
            }
        }
        return parameter;
    }
}
