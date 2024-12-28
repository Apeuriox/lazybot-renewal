package me.aloic.lazybot.osu.dao.entity.optionalattributes.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserBadge implements Serializable {
    private String awarded_at;
    private String description;
    private String image_url;
    private String url;

}
