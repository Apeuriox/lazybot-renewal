package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapsetDTO;


@Data
@TableName("beatmap")
@AllArgsConstructor
@NoArgsConstructor
public class LazybotBeatmap
{
    @TableId(type = IdType.INPUT)
    private Long id;

    private String title;
    private String artist;
    private String version;
    private Double bpm;
    private String checksum;

    public LazybotBeatmap(BeatmapDTO beatmap, BeatmapsetDTO beatmapset) {
        this.id= Long.valueOf(beatmap.getId());
        this.title=beatmapset.getTitle();
        this.artist=beatmapset.getArtist();
        this.version=beatmap.getVersion();
        this.bpm=beatmap.getBpm();
        this.checksum=beatmap.getChecksum();
    }
}
