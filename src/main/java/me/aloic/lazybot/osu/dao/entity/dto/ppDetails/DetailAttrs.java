package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
public class DetailAttrs implements Serializable
{
    private Difficulty difficulty;
    private State state;
    private Double ppAim;
    private Double ppFlashlight;
    private Double ppSpeed;
    private Double ppAccuracy;
    private Double pp;

    private Integer effectiveMissCount;
}
