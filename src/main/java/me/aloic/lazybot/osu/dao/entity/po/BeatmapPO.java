package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Deprecated
@Data
public class BeatmapPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer bid;
    private Integer sid;
    private Double cs;
    private Double ar;
    private Double hp;
    private Double od;
    private Double star_vanilla;
    private String title;
    private String artist;
    private String mapper;
    private String version;
    private Double bpm;
    private Integer length;
    private String mapStatus;
    private Integer max_combo;
    private String bg_url;
}
