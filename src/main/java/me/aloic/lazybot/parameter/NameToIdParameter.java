package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameToIdParameter extends LazybotCommandParameter
{

    private List<String> targets;
    public NameToIdParameter(List<String> targets,String mode) {
         this.setMode(mode);
         this.targets = targets;
    }
    @Override
    public void validateParams()
    {
        if (targets == null)
            return;
       targets = targets.stream().distinct().limit(10).toList();
    }
    public static NameToIdParameter analyzeParameter(List<String> params)
    {
        NameToIdParameter parameter=new NameToIdParameter();
        if(params == null || params.isEmpty()) throw new RuntimeException("此方法必须有参数");
        parameter.setTargets(List.of(String.join(" ", params).split(",")));
        return parameter;
    }
    public static void setupDefaultValue(NameToIdParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }
}
