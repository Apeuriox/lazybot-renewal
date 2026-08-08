package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Pemonlist.com API 返回的关卡排名数据
 */
@Data
@NoArgsConstructor
public class PemonListItem implements Serializable
{
    @JsonProperty("creator")
    private String creator;

    @JsonProperty("level_id")
    private Integer levelId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("placement")
    private Integer placement;

    @JsonProperty("points")
    private Double points;

    @JsonProperty("records")
    private List<RecordEntryPemonList> records;

    @JsonProperty("verifier")
    private PlayerPemonList verifier;

    @JsonProperty("video_id")
    private String videoId;
}
