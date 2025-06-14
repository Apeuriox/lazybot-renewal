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
public class BeatmapParameter extends LazybotCommandParameter
{
    private String mod;
    private Integer bid;
    private Long userIdentity;

    @Override
    public void validateParams()
    {
        if (bid == null) throw new LazybotRuntimeException("[Lazybot] bid为空!");
        if(mod!=null && (mod.length()&1)!=0) throw new LazybotRuntimeException("[Lazybot] 无效mod组合: " + mod);
    }
    public BeatmapParameter(Integer bid) {
        this.bid=bid;
    }
    public static BeatmapParameter analyzeParameter(List<String> params)
    {
        BeatmapParameter parameter=new BeatmapParameter();
        if (!params.isEmpty()) {
            if (params.size() == 1) {
                if(params.getFirst().contains("+")) {
                    String[] paras = params.getFirst().split("\\+");
                    //正则匹配正整数
                    if (CommonTool.isPositiveInteger(paras[0])) {
                        parameter.setBid(Integer.valueOf(paras[0]));
                        parameter.setMod(paras[1]);
                    }
                    else
                        throw new LazybotRuntimeException("[Lazybot] 参数解析错误,length=1, mod=true");
                }
                else {
                    if(CommonTool.isPositiveInteger(params.getFirst()))
                        parameter.setBid(Integer.valueOf(params.getFirst()));
                    else
                        throw new LazybotRuntimeException("[Lazybot] 参数解析错误, length=1, mod=false");
                }
            }
            else {
                    if (CommonTool.isPositiveInteger(params.getFirst())) {
                        parameter.setBid(Integer.valueOf(params.getFirst()));
                        parameter.setMod(params.get(1));
                    }
                    else
                        throw new LazybotRuntimeException("[Lazybot] 输入bid不为正整数");
            }
        }
        return parameter;
    }
    public static void setupDefaultValue(BeatmapParameter parameter, AccessTokenPO accessTokenPO)
    {
    }

}
