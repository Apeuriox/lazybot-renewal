package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.util.CommonTool;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BadgeUserActionParameter extends LazybotCommandParameter
{
    private List<Integer> badgeIds;
    private List<Integer> targetPlayerIds;


    @Override
    public void validateParams()
    {
        if (badgeIds==null||badgeIds.isEmpty()) throw new LazybotRuntimeException("参数不能为空");
        if (badgeIds.size()!=targetPlayerIds.size()) throw new LazybotRuntimeException("参数不对称");
    }
    public BadgeUserActionParameter(List<Integer> targetPlayerId, List<Integer> badgeId) {
        this.targetPlayerIds=targetPlayerId;
        this.badgeIds=badgeId;
    }

    public static BadgeUserActionParameter analyzeParameter(List<String> params)
    {
        BadgeUserActionParameter parameter=new BadgeUserActionParameter();
        List<Integer> badgeIds = new ArrayList<>();
        List<Integer> targetPlayerIds = new ArrayList<>();
        if (params.isEmpty()) throw new LazybotRuntimeException("参数不合法");
        String totalParams = String.join(" ", params);
        String[] singlePart = totalParams.split("\\|");
        for (String part : singlePart)
        {
            String[] partParams = part.split(":");
            if (partParams.length != 2) throw new LazybotRuntimeException("参数不合法");
            if (CommonTool.isPositiveInteger(partParams[0]) && CommonTool.isPositiveInteger(partParams[1]))
            {
                targetPlayerIds.add(Integer.parseInt(partParams[0]));
                badgeIds.add(Integer.parseInt(partParams[1]));
            }
        }
        parameter.setBadgeIds(badgeIds);
        parameter.setTargetPlayerIds(targetPlayerIds);
        return parameter;
    }
}
