package me.aloic.lazybot.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileParameter extends LazybotCommandParameter
{
    private ProfileCustomizationPO profileCustomizationPO;
    @Override
    public void validateParams()
    {

    }
    public ProfileParameter(String playerName, String mode)
    {
        this.setPlayerName(playerName);
        this.setMode(mode);
    }
    public static ProfileParameter analyzeParameter(List<String> params)
    {
        ProfileParameter parameter=new ProfileParameter();
        if (!params.isEmpty()) {
            parameter.setPlayerName(String.join(" ", params));
        }
        return parameter;
    }
    public static void setupDefaultValue(ProfileParameter parameter, AccessTokenPO accessTokenPO)
    {
        if (parameter.getPlayerName() == null)
            parameter.setPlayerName(accessTokenPO.getPlayer_name());
        if (parameter.getMode() == null)
            parameter.setMode(accessTokenPO.getDefault_mode());
    }
}
