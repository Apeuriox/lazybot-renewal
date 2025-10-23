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
    private List<Integer> targetLazybotIds;
    private BadgeManageType actionType;
    public enum BadgeManageType
    {
        ADD, REMOVE
    }


    @Override
    public void validateParams()
    {
        if (badgeIds==null||badgeIds.isEmpty()) throw new LazybotRuntimeException("参数不能为空");
        if (badgeIds.size()!= targetLazybotIds.size()) throw new LazybotRuntimeException("参数不对称");
    }
    public BadgeUserActionParameter(List<Integer> targetLazybotIds, List<Integer> badgeId) {
        this.targetLazybotIds =targetLazybotIds;
        this.badgeIds=badgeId;
    }

    public static BadgeUserActionParameter analyzeParameter(List<String> params)
    {
        BadgeUserActionParameter parameter=new BadgeUserActionParameter();
        if (params==null || params.isEmpty()) throw new LazybotRuntimeException("参数输入不足");
        parameter.setActionType(fromString(params.getFirst()));

        List<Integer> badgeIds = new ArrayList<>();
        List<Integer> targetLazybotIds = new ArrayList<>();
        String totalParams = String.join(" ", params.subList(1, params.size()));
        String[] singlePart = totalParams.split("\\|");
        for (String part : singlePart)
        {
            String[] partParams = part.split(":");
            if (partParams.length != 2) throw new LazybotRuntimeException("参数输入不合法: " + part);
            if (CommonTool.isPositiveInteger(partParams[0]) && CommonTool.isPositiveInteger(partParams[1]))
            {
                targetLazybotIds.add(Integer.parseInt(partParams[0]));
                badgeIds.add(Integer.parseInt(partParams[1]));
            }
        }
        parameter.setBadgeIds(badgeIds);
        parameter.setTargetLazybotIds(targetLazybotIds);
        return parameter;
    }
    private static BadgeManageType fromString(String input)
    {
        return switch (input.toLowerCase())
        {
            case "add","addto","at" -> BadgeManageType.ADD;
            case "remove","rm","removefrom","rf"  -> BadgeManageType.REMOVE;
            default -> throw new LazybotRuntimeException("不兼容的二级命令");
        };
    }
}
