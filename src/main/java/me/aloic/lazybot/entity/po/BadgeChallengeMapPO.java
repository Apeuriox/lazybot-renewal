package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "badge_challenge_map", autoResultMap = true)
public class BadgeChallengeMapPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer challenge_id;
    private Integer beatmap_id;
    private Double required_acc;
    private Integer required_combo;
    private Integer max_accepted_miss;
    private String mods_allowed;
    private Integer mode;


}
