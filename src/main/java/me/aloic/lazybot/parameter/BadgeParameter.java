package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BadgeParameter extends LazybotCommandParameter
{
    private Integer index;
    private BadgeActionType type;
    private List<Integer> indexes;

    public enum BadgeActionType {
        VIEW, LIST, SET, CLEAR
    }

    @Override
    public void validateParams()
    {
        if (this.index!=null && this.index<1) throw new LazybotRuntimeException("{id}输入不合法");
    }
    public BadgeParameter(String playerName, String type) {
        this.setPlayerName(playerName);
        this.type=fromString(type);
    }
    public static BadgeParameter analyzeParameter(List<String> params)
    {
        BadgeParameter parameter=new BadgeParameter();
        if (!params.isEmpty()) {
            parameter.setType(fromString(params.getFirst()));
            if (params.size() >= 2) {
                if (parameter.getType().equals(BadgeActionType.VIEW))
                {
                    try
                    {
                        parameter.index = Integer.parseInt(params.get(1));
                    } catch (Exception e)
                    {
                        throw new LazybotRuntimeException("{index} 输入不合法");
                    }
                }
                else if (parameter.getType().equals(BadgeActionType.SET))
                {
                    try
                    {
                        parameter.indexes = parseIndexes(String.join("", params.subList(1, params.size())));
                    } catch (Exception e)
                    {
                        throw new LazybotRuntimeException("{indexes} 输入不合法");
                    }
                }
            }
        }
        else throw new LazybotRuntimeException("使用方法: /badge list\n或/badge view <id>\n或/badge set <id1,id2,...>");
        return parameter;
    }
    public static void setupDefaultValue(BadgeParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setPlayerName(accessTokenPO.getPlayer_name());
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
    }
    private static BadgeActionType fromString(String input)
    {
        if (input.equalsIgnoreCase("list")) return BadgeActionType.LIST;
        else if (input.equalsIgnoreCase("view")) return BadgeActionType.VIEW;
        else if (input.equalsIgnoreCase("set")) return BadgeActionType.SET;
        else if (input.equalsIgnoreCase("clear")) return BadgeActionType.CLEAR;
        else throw new LazybotRuntimeException("使用方法: /badge list或/badge view <id>");
    }
    private static List<Integer> parseIndexes(String input)
    {
        input=input.replaceAll(" ","");
        String[] brokeIndexes = input.split(",");
        return Arrays.stream(brokeIndexes).map(Integer::parseInt).toList();
    }
}
