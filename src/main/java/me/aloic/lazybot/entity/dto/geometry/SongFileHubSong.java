package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GDSongFileHub API 返回的歌曲数据
 */
@Data
@NoArgsConstructor
public class SongFileHubSong implements Serializable
{
    @JsonProperty("_id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("songURL")
    private String songUrl;

    @JsonProperty("urlHash")
    private String urlHash;

    @JsonProperty("songName")
    private String songName;

    @JsonProperty("ytVideoID")
    private String ytVideoId;

    @JsonProperty("songID")
    private String songId;

    @JsonProperty("state")
    private String state;

    @JsonProperty("filetype")
    private String filetype;

    @JsonProperty("downloadUrl")
    private String downloadUrl;

    @JsonProperty("levelID")
    private String levelId;

    @JsonProperty("__v")
    private Integer v;

    @JsonProperty("downloads")
    private Integer downloads;
}
