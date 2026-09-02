package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("player_stats_watch")
public class PlayerStatsWatchPO implements Serializable
{
    private Integer id;
    private Integer mode;
    private Integer subserver;
    private Boolean active;
    private LocalDateTime updatedAt;
}
