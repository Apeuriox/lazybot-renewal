package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
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
}
