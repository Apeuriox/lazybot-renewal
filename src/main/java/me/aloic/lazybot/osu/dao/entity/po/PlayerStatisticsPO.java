package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.player.GradeCounts;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "player_stats_daily", autoResultMap = true)
public class PlayerStatisticsPO implements Serializable
{
    /** osu user id */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /** {@link me.aloic.lazybot.osu.enums.OsuMode} value */
    private Integer mode;

    /** {@link me.aloic.lazybot.osu.enums.SupportedSubServer} value */
    private Integer subserver;

    /** Snapshot calendar timestamp (Asia/Shanghai start of day). */
    private LocalDateTime recordDateTime;

    private String playerName;
    private Double performancePoint;
    private Integer globalRank;
    private Integer countryRank;
    private Long totalScore;
    private Long rankTotalScore;
    private Double accuracy;
    private Integer playCount;
    private Long totalHitCount;
    private Long totalPlayTime;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private GradeCounts grades;

}
