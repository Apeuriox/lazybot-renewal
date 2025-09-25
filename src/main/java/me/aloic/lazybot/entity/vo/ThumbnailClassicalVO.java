package me.aloic.lazybot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailClassicalVO implements Serializable
{
    private PlayerInfoVO player;
    private ScoreVO score;
    private String comment;
    private String position;
}
