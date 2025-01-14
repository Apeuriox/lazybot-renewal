package me.aloic.lazybot.parameter;

import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;

@Data
public abstract class LazybotCommandParameter
{
    private UserTokenPO accessToken;
    private String playerName;
    private PlayerInfoDTO playerInfo;
    private String mode;

    abstract void validateParams();
}
