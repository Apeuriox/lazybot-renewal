package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Failtimes implements Serializable {
    private Integer[] exit;
    private Integer[] fail;
}
