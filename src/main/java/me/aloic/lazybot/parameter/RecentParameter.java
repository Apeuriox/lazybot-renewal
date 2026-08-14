package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.util.ArgumentParser;
import me.aloic.lazybot.util.Parsers;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentParameter extends LazybotCommandParameter
{
    private Integer index;
    private Integer version;

    private Long channelId;
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
            throw new IllegalArgumentException("索引必须介于 1 和 100 之间");
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

        ArgumentParser parser = ArgumentParser.of(params);
        parser.tryPop(Parsers.INDEX,
                matcher -> recentParameter.setIndex(Integer.parseInt(matcher.group(1))));
        if (recentParameter.getIndex() == null) {
            parser.tryPopIf(Parsers.DIGITS,
                    matcher -> Integer.parseInt(matcher.group()) >= 1
                            && Integer.parseInt(matcher.group()) <= 100,
                    matcher -> recentParameter.setIndex(Integer.parseInt(matcher.group())));
        }
        if (!parser.remainder().isEmpty()) {
            recentParameter.setPlayerName(parser.remainder());
        }
        return recentParameter;
    }
    public static void setupDefaultValue(RecentParameter recentParameter, UserBindingPO accessTokenPO)
    {
        recentParameter.setPlayerId(accessTokenPO.getPlayer_id());
        if (recentParameter.getMode() == null)
            recentParameter.setMode(accessTokenPO.getDefault_mode());
        if (recentParameter.getVersion() == null)
            recentParameter.setVersion(0);
        if (recentParameter.getIndex()==null)
            recentParameter.setIndex(1);
    }
}
