package me.aloic.lazybot.osu.dao.entity.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TeamDTO
{
    private String flag_url;
    private String name;
    private String short_name;
    private Integer id;
}
