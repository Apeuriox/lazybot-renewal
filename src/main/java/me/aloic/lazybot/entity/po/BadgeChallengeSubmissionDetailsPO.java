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
@TableName(value = "badge_challenge_submission_details", autoResultMap = true)
public class BadgeChallengeSubmissionDetailsPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer challenge_id;
    private Integer beatmap_id;
    private Integer player_id;
    private Long score_id;
    private Double achieved_acc;
    private Integer achieved_combo;
    private Integer miss_count;
    private String mod_used;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;



}
