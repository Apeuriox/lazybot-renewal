package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.util.CommonTool;
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

    public BadgeChallengeSubmissionDetailsPO(ScoreLazerDTO score, Integer challengeId)
    {
        this.challenge_id = challengeId;
        this.beatmap_id = score.getBeatmap_id();
        this.player_id = score.getUser_id();
        this.score_id = score.getId();
        this.achieved_acc = score.getAccuracy();
        this.achieved_combo = score.getMax_combo();
        this.miss_count = score.getStatistics().getMiss();
        this.mod_used = CommonTool.modArrayToString(score.getMods());
        this.create_time=LocalDateTime.now();
    }



}
