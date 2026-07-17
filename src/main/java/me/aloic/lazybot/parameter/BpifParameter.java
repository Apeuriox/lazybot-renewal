package me.aloic.lazybot.parameter;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.utils.RosuAlgorithmVersionUtil;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

import java.util.Arrays;
import java.util.List;
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
        this.modList = mod == null || mod.isBlank()
                ? List.of()
                : Arrays.stream(mod.split("(?<=\\G.{2})")).collect(Collectors.toList());
        if (this.modList.isEmpty()) {
            this.operator = "KEEP";
        }
    }

    @Override
    public void validateParams()
    {
        boolean hasMods = this.getModList() != null && !this.getModList().isEmpty();
        if (!hasMods && this.getAlgorithmVersion() == null) {
            throw new LazybotRuntimeException("请提供 Mod 操作或 PP 算法版本，例如 /bpif +HD 或 /bpif @202502");
        }
        if (!hasMods) {
            this.operator = "KEEP";
            this.modList = List.of();
        }
    }
    public static BpifParameter analyzeParameter(List<String> params)
    {
        BpifParameter parameter = new BpifParameter();
        if (params == null || params.isEmpty()) {
            throw new LazybotRuntimeException("参数呢?");
        }

        ArgumentParser parser = ArgumentParser.of(params);
        parser.tryPop(Parsers.ALGORITHM_VERSION,
                matcher -> parameter.setAlgorithmVersion(RosuAlgorithmVersionUtil.parse(matcher.group())));
        parser.tryPop(Parsers.BPIF_MOD_OPERATION,
                matcher -> handleBpIf(parameter, matcher.group()));
        if (!parser.remainder().isEmpty()) {
            parameter.setPlayerName(parser.remainder());
        }
        return parameter;

    }
    private static void handleBpIf(BpifParameter parameter, String modStr)
    {
        String operator = modStr.substring(0, 1);
        if(!operator.equals("+") && !operator.equals("-") && !operator.equals("!")) {
            throw new LazybotRuntimeException("不支持的运算符: " + operator);
        }
        else {
            parameter.setOperator(operator);
            String mods = modStr.substring(1);
            if (mods.endsWith("!")) {
                mods = mods.substring(0, mods.length() - 1);
            }
            parameter.setMod(mods);

            parameter.setModList(parameter.getMod().isBlank()
                    ? List.of()
                    : Arrays.stream(parameter.getMod().split("(?<=\\G.{2})"))
                        .collect(Collectors.toList()));
        }
    }
    public static void setupDefaultValue(BpifParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
        if (parameter.getRenderSize() == null)
            parameter.setRenderSize(30);
        if (parameter.getMod() == null)
            parameter.setMod("");
    }
}
