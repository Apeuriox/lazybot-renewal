package me.aloic.lazybot.parameter;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
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

    }
}
