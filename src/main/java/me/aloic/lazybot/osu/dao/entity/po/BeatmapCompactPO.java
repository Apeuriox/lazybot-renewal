package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@TableName(value = "beatmap_static", autoResultMap = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeatmapCompactPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer bid;
    private Integer sid;
    private Integer max_combo;
    private Integer ruleset_id;

    public BeatmapCompactPO(Integer bid, Integer sid, Integer max_combo, Integer ruleset_id)
    {
        this.bid = bid;
        this.sid = sid;
        this.max_combo = max_combo;
        this.ruleset_id = ruleset_id;
    }
}