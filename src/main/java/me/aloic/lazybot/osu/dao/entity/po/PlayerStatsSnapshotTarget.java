package me.aloic.lazybot.osu.dao.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsSnapshotTarget implements Serializable
{
    private Integer userId;
    private Integer mode;
}
