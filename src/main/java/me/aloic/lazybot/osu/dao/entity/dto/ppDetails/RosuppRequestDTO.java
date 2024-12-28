package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: huhao109
 * 2024/4/18 16:44
 */
@Data
@NoArgsConstructor
public class RosuppRequestDTO implements Serializable {
    private Long beatmapId;
    private String[] mods;
    private Integer n50;
    private Integer n100;
    private Integer n300;
    private Integer combo;
    private Integer misses;
    private Double accuracy;
}
