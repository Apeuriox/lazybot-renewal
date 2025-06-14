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
public class CustomizationParameter extends LazybotCommandParameter
{
    private String targetUrl;
    private String otherParams;
    private String type;
    private Long qqCode;
    @Override
    public void validateParams()
    {
        if (otherParams==null)
            throw new LazybotRuntimeException("[Lazybot] 三级参数无效");
    }
    public CustomizationParameter(String playerName, String type) {
        this.setPlayerName(playerName);
        this.type=type;
    }
    public static CustomizationParameter analyzeParameter(List<String> params)
    {
        CustomizationParameter parameter=new CustomizationParameter();
        if (!params.isEmpty()) {
            if (params.size() >= 2) {
                parameter.setType(params.getFirst());
                parameter.setTargetUrl(String.join(" ", params.subList(1, params.size())));
                parameter.setOtherParams(String.join(" ", params.subList(1, params.size())));
            }
            else throw new LazybotRuntimeException("[Lazybot] 使用方法: /customize <类型> <图片链接>，具体请参考help页面");
        }
        else throw new LazybotRuntimeException("[Lazybot] 使用方法: /customize <类型> <图片链接>，具体请参考help页面");
        return parameter;
    }
    public static void setupDefaultValue(CustomizationParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setPlayerName(accessTokenPO.getPlayer_name());
        parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if(parameter.getQqCode() == null)
            parameter.setQqCode(accessTokenPO.getQq_code());
        parameter.setQqCode(accessTokenPO.getQq_code());
    }
}
