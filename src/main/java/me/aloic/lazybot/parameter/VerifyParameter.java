package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class VerifyParameter extends LazybotCommandParameter
{
    private String type;
    private Long qqCode;
    private Integer customizeId;
    @Override
    public void validateParams()
    {
    }
    public VerifyParameter(Long qqCode, String type) {
        this.qqCode=qqCode;
        this.type=type;
    }
    public static VerifyParameter analyzeParameter(List<String> params)
    {
        VerifyParameter parameter=new VerifyParameter();
        if (!params.isEmpty()) {
            parameter.setType(params.getFirst());
            if(params.size()>=2 && CommonTool.isPositiveInteger(params.get(1))) {
                parameter.setCustomizeId(Integer.valueOf(params.get(1)));
            }
        }
        return parameter;
    }
    public static void setupDefaultValue(VerifyParameter parameter, AccessTokenPO accessTokenPO)
    {
        parameter.setQqCode(accessTokenPO.getQq_code());
    }
}
