package me.aloic.lazybot.parameter;

import lombok.*;
import me.aloic.lazybot.shiro.event.LazybotSlashCommandEvent;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeriesParameter extends LazybotCommandParameter
{
    private Integer maxIndex;
    private Integer version;

    public SeriesParameter(Integer maxIndex, String mode, Integer version, String playerName)
    {
        this.setMode(mode);
        this.maxIndex=maxIndex;
        this.version=version;
        this.setPlayerName(playerName);
    }

    @Override
    public void validateParams() {
        if(maxIndex>200 || maxIndex<0) {
            throw new IllegalArgumentException("最大范围越界: " + maxIndex);
        }
        if(version==null) {
            version=0;
        }
    }
    public static SeriesParameter analyzeParameter(List<String> params)
    {
        SeriesParameter result=new SeriesParameter();
        if (!params.isEmpty()) {
            String last = params.getLast();
            if (last.matches("\\d+")) {
                result.setMaxIndex(Integer.parseInt(last));
                params.removeLast();
            }
            else {
                result.setMaxIndex(21);
            }
            result.setPlayerName(String.join(" ", params));
        }
        else {
            result.setMaxIndex(21);
        }
        return result;
    }
    public static void setupDefaultValue(SeriesParameter scoreParameter, @NonNull Integer playerId, @NonNull String mode)
    {
        scoreParameter.setPlayerId(playerId);
        if (scoreParameter.getMode() == null)
            scoreParameter.setMode(mode);
        if (scoreParameter.getVersion() == null)
            scoreParameter.setVersion(0);
    }
    public static SeriesParameter setupParameter(LazybotSlashCommandEvent event, @NonNull Integer playerId, @NonNull String mode)
    {
        SeriesParameter params=SeriesParameter.analyzeParameter(event.getCommandParameters());
        SeriesParameter.setupDefaultValue(params, playerId, mode);
        if(event.getOsuMode()!=null)
            params.setMode(event.getOsuMode().getDescribe());
        params.validateParams();
        return params;
    }

}
