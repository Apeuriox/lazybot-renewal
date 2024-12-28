package me.aloic.lazybot.osu.dao.entity.dto.sayobot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BeatmapInfoDTO implements Serializable
{
    private Integer status;
    private SayoData data;
}
