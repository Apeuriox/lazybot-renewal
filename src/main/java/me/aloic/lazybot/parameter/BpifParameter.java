package me.aloic.lazybot.parameter;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BpifParameter extends LazybotCommandParameter
{
    private String operator;
    private String mod;
    private List<String> modList;
    private Integer renderSize;

    public BpifParameter(@Nonnull String playerName,@Nonnull String mode,@Nonnull String operator,@Nonnull String mod,Integer renderSize)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
        this.operator=operator;
        this.mod=mod;
        this.renderSize=renderSize;
        this.modList = Arrays.stream(mod.split("(?<=\\G.{2})"))
                .collect(Collectors.toList());
    }

    @Override
    public void validateParams()
    {
        if (this.getModList().isEmpty()) throw new RuntimeException("Mod在哪?");
        if ((this.getModList().size()&1)==0) throw new RuntimeException("Mod长度不为偶数,size=" + this.getModList().size());

    }
    public static BpifParameter analyzeParameter(List<String> params)
    {
        BpifParameter parameter=new BpifParameter();
        String modStr;
        if (params == null||params.isEmpty()) throw new RuntimeException("参数呢?");
        else {
            if (params.size() == 1) {
                modStr=params.getFirst();
                handleBpIf(parameter, modStr);
            }
            else if(params.size() == 2)
            {
                parameter.setPlayerName(params.getFirst());
                modStr=params.get(1);
                handleBpIf(parameter, modStr);
            }
        }
        return parameter;

    }
    private static void handleBpIf(BpifParameter parameter, String modStr)
    {
        String operator=modStr.substring(0,1);
        if ( modStr.endsWith("!")) operator="!";
        if(!operator.equals("+") && !operator.equals("-") && !operator.equals("!")) {
            throw new RuntimeException("不支持的运算符: " + operator);
        }
        else {
            parameter.setOperator(operator);
            if(operator.equals("!")) parameter.setMod(modStr.substring(1, modStr.length()-1));
            else parameter.setMod(modStr.substring(1));

            parameter.setModList(Arrays.stream(parameter.getMod().split("(?<=\\G.{2})"))
                    .collect(Collectors.toList()));
        }
    }
    public static void setupDefaultValue(BpifParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
        if (parameter.getRenderSize() == null)
            parameter.setRenderSize(30);
        if (parameter.getMod() == null)
            parameter.setMod("");
    }
}
