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
    private Integer hue;
    private String check_sum;
    private String artist;
    private String title;
    private String mapper;

    public BeatmapCompactPO(Integer bid, Integer sid, Integer max_combo, Integer ruleset_id,String check_sum)
    {
        this.bid = bid;
        this.sid = sid;
        this.max_combo = max_combo;
        this.ruleset_id = ruleset_id;
        this.check_sum=check_sum;
    }
    public BeatmapCompactPO(Integer bid, Integer sid, Integer max_combo, Integer ruleset_id,Integer hue ,String check_sum, String artist,String title, String mapper)
    {
        this.bid = bid;
        this.sid = sid;
        this.max_combo = max_combo;
        this.ruleset_id = ruleset_id;
        this.hue=hue;
        this.check_sum=check_sum;
        this.artist=artist;
        this.title=title;
        this.mapper=mapper;
    }
}