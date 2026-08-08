package me.aloic.lazybot.osu.dao.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Read-only projection joining lazybot_user, platform_identity and osu_account.
 * OAuth credentials are deliberately excluded from this object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBindingPO implements Serializable
{
    private Integer id;
    private Long platform_identity_id;
    private String platform;
    private String platform_user_id;
    private Long osu_account_id;
    private Integer player_id;
    private String player_name;
    private String server;
    private String link_method;
    private LocalDateTime verified_at;
    private String default_mode;
    private String default_subset;
    private Integer preferred_panel_version;
    private Boolean enabled;
    private String avatar_etag;
    private LocalDateTime avatar_last_checked;
    private LocalDateTime avatar_next_check_at;

    public String toSimpleString()
    {
        return "[Lazybot] 该用户的绑定情况如下\nLazybot ID: " + id
                + "\n缓存的用户名: " + player_name
                + "\nBancho Osu ID: " + player_id
                + "\n绑定方式: " + link_method
                + "\n默认查询模式: " + default_mode;
    }
    /** Compatibility accessor for existing QQ command parameters. */
    public Long getQqCode()
    {
        if (!"qq".equalsIgnoreCase(platform) || platform_user_id == null) {
            return null;
        }
        return Long.parseLong(platform_user_id);
    }

    /** Compatibility accessor for existing Discord commands. */
    public Long getDiscordCode()
    {
        if (!"discord".equalsIgnoreCase(platform) || platform_user_id == null) {
            return null;
        }
        return Long.parseLong(platform_user_id);
    }

    /**
     * Avatar URLs are stable now; rendering uses the locally cached file keyed by player id.
     */
    public String getAvatar_url()
    {
        return player_id == null ? null : "https://a.ppy.sh/" + player_id;
    }
}
