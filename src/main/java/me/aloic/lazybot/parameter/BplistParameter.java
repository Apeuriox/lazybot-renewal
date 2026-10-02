package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

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
    public BplistParameter(Integer playerId, String mode, Integer from, Integer to)
    {
        this.setPlayerId(playerId);
        this.setMode(mode);
        this.from = from;
        this.to = to;
    }
    public BplistParameter(String playerName,Integer playerId, String mode, Integer from, Integer to)
    {
        this.setPlayerName(playerName);
        this.setPlayerId(playerId);
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
        if (params == null || params.isEmpty())
            throw new LazybotRuntimeException("请输入范围，例/bplist 1-100");

        ArgumentParser parser = ArgumentParser.of(params);
        final boolean[] hasRange = {false};
        parser.tryPop(Parsers.RANGE, matcher -> {
            setupParameterIndexes(parameter, matcher.group());
            hasRange[0] = true;
        });
        if (!hasRange[0]) {
            throw new LazybotRuntimeException("请输入正确的范围，例: /bplist 1-100 @202502");
        }
        if (!parser.remainder().isEmpty()) {
            parameter.setPlayerName(parser.remainder());
        }
        return parameter;
    }

    public static void setupDefaultValue(BplistParameter parameter, UserBindingPO accessTokenPO)
    {
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
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
