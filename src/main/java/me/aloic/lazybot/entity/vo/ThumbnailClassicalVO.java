package me.aloic.lazybot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.graphics.mapping.documentMapper.ThumbnailSVGMapper;
import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailClassicalVO implements Serializable
{
    private PlayerInfoVO player;
    private ScoreVO score;
    private String comment;
    private String position;
    private List<ThumbnailSVGMapper.ThumbnailClassicalAttribute> attributes;

    public ThumbnailClassicalVO(PlayerInfoVO player, ScoreVO score, String comment, String position)
    {
        this.player = player;
        this.score = score;
        this.comment = comment;
        this.position = position;
        this.attributes= List.of(ThumbnailSVGMapper.ThumbnailClassicalAttribute.AR,ThumbnailSVGMapper.ThumbnailClassicalAttribute.CS);
    }
}
