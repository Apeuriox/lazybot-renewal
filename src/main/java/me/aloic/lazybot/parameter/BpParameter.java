package me.aloic.lazybot.parameter;

import lombok.*;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String text = String.join(" ", params).trim();
        if (text.matches("\\d+")) {
            int indexVal = Integer.parseInt(text);
            if (indexVal >= 1 && indexVal <= 200)
                bpParameter.index = indexVal;
            else
                bpParameter.setPlayerName(text);
        }
        else {
            Matcher indexMatcher = Pattern.compile("#(\\d+)").matcher(text);
            if (indexMatcher.find()) {
                bpParameter.index = Integer.parseInt(indexMatcher.group(1));
                text = text.replace(indexMatcher.group(), "").trim();
            }
            if (!text.isEmpty()) bpParameter.setPlayerName(text);
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
