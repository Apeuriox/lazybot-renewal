package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GDDL 关卡的元数据 (Meta)
 */
@Data
@NoArgsConstructor
public class LevelMetadata implements Serializable
{
    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("SongID")
    private Integer songId;

    @JsonProperty("Length")
    private Integer length;

    @JsonProperty("IsTwoPlayer")
    private Boolean isTwoPlayer;

    @JsonProperty("Difficulty")
    private String difficulty;

    @JsonProperty("Song")
    private DemonLadderSong song;

    @JsonProperty("Publisher")
    private DemonLadderPublisher publisher;
}
