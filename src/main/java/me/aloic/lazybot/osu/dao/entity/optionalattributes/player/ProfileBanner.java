package me.aloic.lazybot.osu.dao.entity.optionalattributes.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ProfileBanner implements Serializable {
    private Integer id;
    private Integer tournament_id;
    private String image;

}
