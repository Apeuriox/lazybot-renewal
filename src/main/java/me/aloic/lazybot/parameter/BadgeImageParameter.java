package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BadgeImageParameter extends LazybotCommandParameter
{
    private String targetUrl;
    private Integer badgeId;
    @Override
    public void validateParams()
    {
        if (this.badgeId==null || targetUrl==null) throw new LazybotRuntimeException("解析参数为空");
    }
    public BadgeImageParameter(String playerName, String type) {

    }
    public static BadgeImageParameter analyzeParameter(List<String> params)
    {
        BadgeImageParameter parameter=new BadgeImageParameter();
        try{
            if (!params.isEmpty()) {
                if (params.size() >= 2) {
                    parameter.setBadgeId(Integer.valueOf(params.getFirst()));
                    parameter.setTargetUrl(String.join(" ", params.subList(1, params.size())));
                }
                else throw new LazybotRuntimeException("参数不足");
            }
            else throw new LazybotRuntimeException("输入参数为空");
            return parameter;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new LazybotRuntimeException("参数解析失败");
        }

    }
    public static void setupDefaultValue(BadgeImageParameter parameter, AccessTokenPO accessTokenPO)
    {

    }
}
