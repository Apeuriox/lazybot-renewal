package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.Kudosu;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PlayerInfoDTO extends UserCompactDTO
{
    private String cover_url;
    private String discord;
    private Boolean has_supported;
    private String interests;
    private String join_date;
    private Kudosu kudosu;
    private String location;
    private Integer max_blocks;
    private Integer max_friends;
    private String occupation;
    private String playmode;
    private String[] playstyle;
    private Integer post_count;
    private String[] profile_order;
    private String title;
    private String title_url;
    private String twitter;
    private String website;
}
