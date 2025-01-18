package me.aloic.lazybot.parameter;
import lombok.Data;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;

@Data
public abstract class LazybotCommandParameter
{
    private String accessToken;
    private String playerName;

    private Integer playerId;

    private PlayerInfoDTO infoDTO;
    private String mode;

    abstract void validateParams();
}
