package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateParameter extends LazybotCommandParameter
{
    private String type;


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
                throw new RuntimeException("update avatar {username} or Update track {username}");
            }
            else {
                parameter.setType(params.getFirst());
                parameter.setPlayerName(params.get(1));
            }
        }
        return parameter;
    }
    public static void setupDefaultValue(UpdateParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }

}
