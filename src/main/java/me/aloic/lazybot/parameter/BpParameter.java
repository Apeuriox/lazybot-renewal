package me.aloic.lazybot.parameter;

import lombok.*;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BpParameter extends LazybotCommandParameter
{
    private Integer version;
    private Integer index;

    private Long channelId;
    private static final int MAX_INDEXED = 200;

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
        if (index<=0||index > MAX_INDEXED) {
            throw new LazybotRuntimeException("Bp查询区间为 1 到 " + MAX_INDEXED);
        }
        if(version==null) {
            version=0;
        }
    }
    public static BpParameter analyzeParameter(List<String> params)
    {
        BpParameter bpParameter=new BpParameter();
        if (params == null || params.isEmpty())
            return new BpParameter(null,null,0,1);

        ArgumentParser parser = ArgumentParser.of(params);
        parser.tryPop(Parsers.INDEX,
                matcher -> bpParameter.setIndex(Integer.parseInt(matcher.group(1))));
        if (bpParameter.getIndex() == null) {
            parser.tryPopIf(Parsers.DIGITS,
                    matcher -> Integer.parseInt(matcher.group()) >= 1
                            && Integer.parseInt(matcher.group()) <= MAX_INDEXED,
                    matcher -> bpParameter.setIndex(Integer.parseInt(matcher.group())));
        }
        if (!parser.remainder().isEmpty()) {
            bpParameter.setPlayerName(parser.remainder());
        }
        return bpParameter;
    }
    public static void setupDefaultValue(BpParameter bpParameter, @NonNull String mode)
    {
        if (bpParameter.getMode() == null)
            bpParameter.setMode(mode);
        if (bpParameter.getVersion() == null)
            bpParameter.setVersion(0);
        if (bpParameter.getIndex()==null)
            bpParameter.setIndex(1);

    }
}
