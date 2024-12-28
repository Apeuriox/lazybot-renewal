package me.aloic.lazybot.osu.dao.entity.dto.sayobot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Bid_data implements Serializable
{
    private Integer bid;
    private Integer mode;
    private String version;
    private Double aim;
    private Double speed;
    private Double pp_aim;
    private Double pp_speed;
    private Double pp_acc;
    private Integer circles;
    private Integer sliders;
    private Integer spinners;
    private String bg;
}
