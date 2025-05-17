package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WhatIfParameter extends LazybotCommandParameter
{
    private Map<Double,Integer> insertionMap;
    @Override
    public void validateParams()
    {
        if (insertionMap==null) throw new LazybotRuntimeException("输入参数为空");
    }

    public static WhatIfParameter analyzeParameter(List<String> params)
    {
        WhatIfParameter parameter=new WhatIfParameter();
        if (params.isEmpty()) throw new LazybotRuntimeException("请输入参数");
        else {
            parameter.setInsertionMap(WhatIfParameter.parseToMap(String.join(" ", params)));
        }
        return parameter;
    }
    public static void setupDefaultValue(WhatIfParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setPlayerName(accessTokenPO.getPlayer_name());
        parameter.setMode(accessTokenPO.getDefault_mode());
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        parameter.setAccessToken(accessTokenPO.getAccess_token());
    }
    public static Map<Double, Integer> parseToMap(String input) {
        Map<Double, Integer> result = new HashMap<>();
        String[] parts = input.trim().split("\\s+");
        for (String part : parts) {
            double key;
            int count;
            try{
                if (part.trim().contains("*")) {
                    String[] split = part.split("\\*");
                    key = Double.parseDouble(split[0]);
                    count = Integer.parseInt(split[1]);
                } else {
                    key = Double.parseDouble(part);
                    count = 1;
                }
                if (key<=0.0) throw new LazybotRuntimeException("输入的pp值不能小于等于0");
                if (key>=3000) throw new LazybotRuntimeException("本指令暂不支持外星人使用");
                if (count<=0) throw new LazybotRuntimeException("输入的次数不能小于等于0");
                if (count>100) throw new LazybotRuntimeException("bp会被裁剪成100长度，所以我是不是应该请你当测试员呢");
            }
            catch (LazybotRuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new LazybotRuntimeException("解析参数时出错: " + part);
            }
            result.merge(key, count, Integer::sum);
        }
        return result;
    }
}
