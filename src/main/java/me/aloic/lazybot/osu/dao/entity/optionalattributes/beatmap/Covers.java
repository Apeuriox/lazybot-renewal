package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Covers implements Serializable {
    private String cover;
    @JSONField(name = "cover@2x")
    private String cover2x;
    private String card;
    @JSONField(name = "card@2x")
    private String card2x;
    private String list;
    @JSONField(name = "list@2x")
    private String list2x;
    private String slimcover;
    @JSONField(name = "slimcover@2x")
    private String slimcover2x;
}
