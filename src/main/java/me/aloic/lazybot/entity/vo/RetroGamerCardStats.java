package me.aloic.lazybot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetroGamerCardStats implements Serializable
{
    private String avatarUrl;
    private String name;
    private Integer id;

    public RetroGamerCardStats(AccessTokenPO token)
    {
        this.avatarUrl = AssertDownloadUtil.avatarAbsolutePath(token.getPlayer_id());
        this.name = token.getPlayer_name();
        this.id = token.getPlayer_id();
    }
}
