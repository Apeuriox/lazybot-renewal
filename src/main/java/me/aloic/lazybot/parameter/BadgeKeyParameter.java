package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BadgeKeyParameter extends LazybotCommandParameter
{
    private Integer maxUses;
    private Integer badgeId;
    private Integer expireTime;
    private Boolean isMultiKey;

    @Override
    public void validateParams()
    {
    }

    public static BadgeKeyParameter analyzeParameter(List<String> params)
    {
        BadgeKeyParameter parameter=new BadgeKeyParameter();
        if (!params.isEmpty() && params.size()>3) {
           try{
               parameter.setBadgeId(Integer.parseInt(params.getFirst()));
               parameter.setMaxUses(Integer.parseInt(params.get(1)));
               parameter.setExpireTime(Integer.parseInt(params.get(2)));
               parameter.setIsMultiKey(params.get(3).equalsIgnoreCase("true"));
               return parameter;
           }
           catch (Exception e)
           {
               throw new LazybotRuntimeException("使用方法: /genkey <badgeId> <MaxUses> <expireTime> <isMultiKey>");
           }
        }
        else throw new LazybotRuntimeException("参数不足");
    }
    public static void setupDefaultValue(BadgeKeyParameter parameter, UserBindingPO accessTokenPO)
    {
    }

}
