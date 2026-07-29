package me.aloic.lazybot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.entity.po.CardUserPointsPO;
import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import me.aloic.lazybot.osu.utils.AssetDownloadUtil;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckInStats implements Serializable
{
    private int totalCheckIns;
    private int continuousCheckIns;
    private int lazyCoins;
    private int lazyCoinsDiff;
    private int totalLazyCoins;
    private String playerName;
    private String avatar_url;

    public CheckInStats(CardUserPointsPO player,int coin, UserBindingPO token)
    {
        this.totalCheckIns = player.getAccumulated_check_time()+1;
        this.continuousCheckIns = player.getContinuous_check_time();
        this.lazyCoins = player.getPoints()+coin;
        this.lazyCoinsDiff = coin;
        this.totalLazyCoins = player.getTotal_history_points()+coin;
        this.playerName = token.getPlayer_name();
        this.avatar_url = AssetDownloadUtil.avatarAbsolutePath(token.getPlayer_id());

    }
}
