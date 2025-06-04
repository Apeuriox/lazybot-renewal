package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BplistParameter extends LazybotCommandParameter
{
    private Integer from;
    private Integer to;
    private static final int MAX_RANGE = 100;
    private static final int MAX_INDEXED = 200;

    public BplistParameter(String playerName, String mode, Integer from, Integer to)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.from = from;
        this.to = to;
    }

    @Override
    public void validateParams()
    {
        if (from > 200 || to > 200) throw new IllegalArgumentException("{FROM} 和 {TO}必须大于: " + MAX_INDEXED);
        if (from >= to) throw new IllegalArgumentException("{FROM} 必须小于 {TO}");
        if (from <= 0) throw new IllegalArgumentException("{FROM} 和 {TO} 必须大于0");
        if (to-from> MAX_RANGE) throw new IllegalArgumentException("请不要一次性请求渲染过多成绩，现在最大限制为: " + MAX_RANGE);
    }

    public static BplistParameter analyzeParameter(List<String> params)
    {
        BplistParameter parameter = new BplistParameter();
        if (params == null)
            throw new LazybotRuntimeException("请输入范围，例/bplist 1-100");
        else if (params.size() == 1)
                setupParameterIndexes(parameter, params.getFirst());
        else if (params.size() > 1)
        {
            setupParameterIndexes(parameter, params.get(1));
            parameter.setPlayerName(params.get(0));
        }
        else throw new LazybotRuntimeException("请检查输入参数");
        return parameter;
    }

    public static void setupDefaultValue(BplistParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null) parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (parameter.getMode() == null) parameter.setMode(accessTokenPO.getDefault_mode());
    }
    private static void setupParameterIndexes(BplistParameter parameter,String params)
    {
        try
        {
            String[] fromAndTo = params.split("-");
            if (fromAndTo.length != 2)
                throw new LazybotRuntimeException("请输入正确的范围: " + params);
            int offset = Integer.parseInt(fromAndTo[0]);
            int endsAt = Integer.parseInt(fromAndTo[1]);
            int totalCount = endsAt - offset + 1;
            if (offset <= 0 || totalCount <= 0 || offset > MAX_INDEXED-1 || endsAt > MAX_INDEXED)
                throw new LazybotRuntimeException("请输入正确的范围");
            parameter.setFrom(offset);
            parameter.setTo(endsAt);
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("参数错误: " + e.getMessage());
        }
    }

}
