package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: huhao109
 * 2024/4/19 14:13
 */
@Data
public class State implements Serializable {
    private Integer maxCombo;
    private Integer nGeki;
    private Integer nKatu;
    private Integer n300;
    private Integer n100;
    private Integer n50;
    private Integer misses;
}
