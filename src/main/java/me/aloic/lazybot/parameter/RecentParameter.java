package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RecentParameter extends LazybotCommandParameter
{
    private Integer index;
    private Integer version;
    public RecentParameter(String mode, Integer index, Integer version, String playerName)
    {
        this.setMode(mode);
        this.index=index;
        this.version=version;
        this.setPlayerName(playerName);
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
