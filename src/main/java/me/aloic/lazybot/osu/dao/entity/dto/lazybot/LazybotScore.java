package me.aloic.lazybot.osu.dao.entity.dto.lazybot;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.vo.PPPlusPerformance;

import java.time.LocalDateTime;

@Data
@TableName("scores")
@NoArgsConstructor
@AllArgsConstructor
public class LazybotScore
{
    @TableId(type = IdType.INPUT)
    private Long id;

    private Long playerId;
    private Long beatmapId;

    private Double accuracy;
    private Double ppSpeed;
    private Double ppStamina;
    private Double ppPrecision;
    private Double ppAim;
    private Double ppJump;
    private Double ppFlow;
    private Double ppAccuracy;

    private Integer combo;
    private Double pp;

    private LocalDateTime createdAt;

    public LazybotScore(ScoreLazerDTO scoreLazerDTO, PPPlusPerformance performance) {
        this.id=scoreLazerDTO.getId();
        this.playerId= Long.valueOf(scoreLazerDTO.getUser_id());
        this.beatmapId= Long.valueOf(scoreLazerDTO.getBeatmap_id());
        this.accuracy=scoreLazerDTO.getAccuracy();
        this.combo=scoreLazerDTO.getMax_combo();
        this.createdAt= LocalDateTime.now();
        this.pp=Double.isNaN(performance.getPp())?0.0:performance.getPp();
        this.ppSpeed=Double.isNaN(performance.getPpSpeed())?0.0:performance.getPpSpeed();
        this.ppStamina=Double.isNaN(performance.getPpStamina())?0.0:performance.getPpStamina();
        this.ppPrecision=Double.isNaN(performance.getPpPrecision())?0.0:performance.getPpPrecision();
        this.ppAim=Double.isNaN(performance.getPpAim())?0.0:performance.getPpAim();
        this.ppJump=Double.isNaN(performance.getPpJumpAim())?0.0:performance.getPpJumpAim();
        this.ppFlow=Double.isNaN(performance.getPpFlowAim())?0.0:performance.getPpFlowAim();
        this.ppAccuracy=Double.isNaN(performance.getPpAcc())?0.0:performance.getPpAcc();
    }


}
