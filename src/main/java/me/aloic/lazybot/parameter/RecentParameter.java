package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
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
            throw new IllegalArgumentException("[Lazybot] 索引必须介于 1 和 100 之间");
        }
        if(version==null) {
            version=0;
        }
    }

    public static RecentParameter analyzeParameter(List<String> params)
    {
        RecentParameter recentParameter=new RecentParameter();
        if (params == null || params.isEmpty())
            return new RecentParameter(null,1,0,null);

        String text = String.join(" ", params).trim();
        if (text.matches("\\d+")) {
            int indexVal = Integer.parseInt(text);
            if (indexVal >= 1 && indexVal <= 200)
                recentParameter.index = indexVal;
            else
                recentParameter.setPlayerName(text);
        }
        else {
            Matcher indexMatcher = Pattern.compile("#(\\d+)").matcher(text);
            if (indexMatcher.find()) {
                recentParameter.index = Integer.parseInt(indexMatcher.group(1));
                text = text.replace(indexMatcher.group(), "").trim();
            }
            if (!text.isEmpty()) recentParameter.setPlayerName(text);
        }
        return recentParameter;
    }
    public static void setupDefaultValue(RecentParameter recentParameter, AccessTokenPO accessTokenPO)
    {
        recentParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (recentParameter.getMode() == null)
            recentParameter.setMode(accessTokenPO.getDefault_mode());
        if (recentParameter.getVersion() == null)
            recentParameter.setVersion(0);
        if (recentParameter.getIndex()==null)
            recentParameter.setIndex(0);
    }
}
