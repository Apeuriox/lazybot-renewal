package me.aloic.lazybot.osu.dao.entity.dto.sayobot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SayobotBeatmap implements Serializable
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

    @Override
    public String toString()
    {
        return "SayobotBeatmap{" +
                "bid=" + bid +
                ", mode=" + mode +
                ", version='" + version + '\'' +
                ", aim=" + aim +
                ", speed=" + speed +
                ", pp_aim=" + pp_aim +
                ", pp_speed=" + pp_speed +
                ", pp_acc=" + pp_acc +
                ", circles=" + circles +
                ", sliders=" + sliders +
                ", spinners=" + spinners +
                ", bg='" + bg + '\'' +
                '}';
    }
}
