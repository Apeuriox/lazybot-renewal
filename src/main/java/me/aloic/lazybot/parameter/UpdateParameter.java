package me.aloic.lazybot.parameter;

import lombok.*;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateParameter extends LazybotCommandParameter
{
    private String type;
    private Integer starMoonId;


    @Override
    public void validateParams()
    {

    }
    public UpdateParameter(String playerName, String type)
    {
        this.setPlayerName(playerName);
        this.type=type;
    }
    public static UpdateParameter analyzeParameter(List<String> params)
    {
        UpdateParameter parameter=new UpdateParameter();
        if (!params.isEmpty()) {
            if (params.size() == 1) {
                parameter.setType(params.getFirst());
            }
            else if (params.size() == 2) {
                parameter.setType(params.getFirst());
                parameter.setPlayerName(params.get(1));
            }
            else throw new LazybotRuntimeException("update avatar ${user_name} or Update track ${user_name}");
        }
        return parameter;
    }
    public static void setupDefaultValue(UpdateParameter parameter, @NonNull Integer playerId, @NonNull String mode)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerId(playerId);
        if (parameter.getMode() == null)
            parameter.setMode(mode);
    }

}
