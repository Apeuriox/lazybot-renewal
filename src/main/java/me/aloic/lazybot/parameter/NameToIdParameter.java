package me.aloic.lazybot.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
public class NameToIdParameter extends LazybotCommandParameter
{
    private List<String> targets;
    @Override
    void validateParams()
    {
        if (targets == null)
            return;
       targets = targets.stream().distinct().limit(10).toList();
    }
}
