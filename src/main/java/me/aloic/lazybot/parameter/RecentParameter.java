package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.util.CommonTool;
import java.util.List;

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
        if (params != null && !params.isEmpty()) {
            if (params.size() > 2)
                throw new LazybotRuntimeException("[Lazybot] 允许参数长度最大为2（如果你名字有空格请把空格换成下划线），使用例：/pr userName #1");
            else if (params.size() == 1) {
                if (params.getFirst().contains("#") && CommonTool.isPositiveInteger(params.getFirst().substring(1))) {
                    int targetIndex = Integer.parseInt(params.getFirst().substring(1));
                    targetIndex = Math.min(targetIndex, 50);
                    recentParameter.setIndex(targetIndex);
                }
                else if(!params.getFirst().contains("#")) {
                    recentParameter.setPlayerName(params.getFirst());
                    recentParameter.setIndex(1);
                }
                else
                    throw new LazybotRuntimeException("[Lazybot] 参数错误，含有非数字，使用例：/pr userName #10");
            }
            else if (params.size() == 2) {
                if(params.get(1).contains("#")) {
                    String[] paras = params.get(1).split("#");
                    recentParameter.setPlayerName(params.get(0));
                    if (CommonTool.isPositiveInteger(paras[1])) {
                        int targetIndex = Integer.parseInt(paras[1]);
                        targetIndex = Math.min(targetIndex, 50);
                        recentParameter.setIndex(targetIndex);
                    }
                    else
                    {
                        throw new LazybotRuntimeException("[Lazybot] 参数解析错误，使用例：/pr userName #10");
                    }
                }
                else {
                    recentParameter.setPlayerName(params.get(0));
                    if (CommonTool.isPositiveInteger(params.get(1))) {
                        int targetIndex = Integer.parseInt(params.get(1));
                        targetIndex = Math.min(targetIndex, 50);
                        recentParameter.setIndex(targetIndex);
                    }
                    else {
                        throw new LazybotRuntimeException("[Lazybot] 参数解析错误，使用例：/pr userName #10");
                    }
                }
            }
            else recentParameter.setIndex(1);
        }else recentParameter.setIndex(1);
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
