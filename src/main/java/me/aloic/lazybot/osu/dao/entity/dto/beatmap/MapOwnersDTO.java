package me.aloic.lazybot.osu.dao.entity.dto.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class MapOwnersDTO implements Serializable
{
    private Integer id;
    private String username;
}
