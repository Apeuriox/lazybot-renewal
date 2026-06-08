package me.aloic.lazybot.entity.dto.geometry;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GD关卡音乐信息
 */
@Data
@NoArgsConstructor
public class LevelSong implements Serializable
{
    private String songId;
    private String songName;
    private String artistId;
    private String artistName;
    private String songSize;
    private String videoId;
    private String songLink;
    private String songYoutubeUrl;
    private String isVerified;
}
