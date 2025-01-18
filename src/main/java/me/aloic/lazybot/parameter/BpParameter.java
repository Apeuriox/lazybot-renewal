package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BpParameter extends LazybotCommandParameter
{
    private Integer version;
    private Integer index;
    public BpParameter(String playerName, String mode, Integer version, Integer index)
    {
        this.index=index;
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.version=version;
    }

    @Override
    public void validateParams()
    {
        if (index<=0||index>100) {
            throw new IllegalArgumentException("index must be between 1 and 100");
        }
        if(version!=0) {
            version=1;
        }
    }
    public static BpParameter analyzeParameter(List<String> params)
    {
        BpParameter bpParameter=new BpParameter();
        if (params != null)
        {
            if (params.size() == 1) {
                if(params.getFirst().contains("#")) {
                    String[] paras = params.getFirst().split("#");
                    if (CommonTool.isPositiveInteger(paras[1]) && Integer.parseInt(paras[1]) <= 100)
                        bpParameter.setIndex(Integer.parseInt(paras[1]));
                    else
                        throw new RuntimeException("输入参数不为正整数: " + paras[1]);
                }
                else if(CommonTool.isPositiveInteger(params.getFirst()) && Integer.parseInt(params.getFirst())<=100)
                    bpParameter.setIndex(Integer.parseInt(params.getFirst()));
                else {
                    bpParameter.setPlayerName(params.getFirst());
                    bpParameter.setIndex(1);
                }
            }
            else if (params.size() == 2) {
                if(params.get(1).contains("#")) {
                    String[] paras =params.get(1).split("#");
                    bpParameter.setPlayerName(params.getFirst());
                    if (CommonTool.isPositiveInteger(paras[1]) && Integer.parseInt(paras[1]) <= 100)
                        bpParameter.setIndex(Integer.parseInt(paras[1]));
                    else
                        throw new RuntimeException("输入参数不为正整数: " + paras[1]);
                }
                else if(CommonTool.isPositiveInteger(params.get(1)) && Integer.parseInt(params.get(1))<=100) {
                    bpParameter.setPlayerName(params.getFirst());
                    bpParameter.setIndex(Integer.parseInt(params.get(1)));
                }
            }
            else {
                throw new RuntimeException("Incorrect parameters: " + params);
            }
        }
        else {
            bpParameter.setIndex(1);
        }
        return bpParameter;
    }
    public static void setupDefaultValue(BpParameter bpParameter, AccessTokenPO accessTokenPO)
    {
        if (bpParameter.getPlayerName() == null)
            bpParameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (bpParameter.getMode() == null)
            bpParameter.setMode(accessTokenPO.getDefault_mode());
        if (bpParameter.getVersion() == null)
            bpParameter.setVersion(1);
        if (bpParameter.getIndex()==null)
            bpParameter.setIndex(0);

    }
}
