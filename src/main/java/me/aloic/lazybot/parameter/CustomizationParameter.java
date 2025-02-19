package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CustomizationParameter extends LazybotCommandParameter
{
    private String targetUrl;
    private String type;
    private Long qqCode;
    @Override
    public void validateParams()
    {
        if(!this.getTargetUrl().startsWith("http://") || !this.getTargetUrl().startsWith("https://"))
            throw new RuntimeException("超链接协议无效");
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
            }
            else throw new RuntimeException("使用方法: /customize <类型> <图片链接>");
        }
        return parameter;
    }
    public static void setupDefaultValue(CustomizationParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getPlayerId() == null)
            parameter.setPlayerId(accessTokenPO.getPlayer_id());
        if(parameter.getQqCode() == null)
            parameter.setQqCode(accessTokenPO.getQq_code());
        parameter.setQqCode(accessTokenPO.getQq_code());
    }
}
