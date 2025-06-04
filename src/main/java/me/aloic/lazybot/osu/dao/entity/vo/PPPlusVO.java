package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.Data;
@Data
public class PPPlusVO
{
    private PlayerInfoVO player;

    private double total;
    private double acc;
    private double sta;
    private double spd;
    private double jump;
    private double flow;
    private double pre;

}
