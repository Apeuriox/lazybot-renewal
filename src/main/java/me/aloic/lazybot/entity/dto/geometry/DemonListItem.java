package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Pointercrate Demonlist API 返回的关卡排名数据
 */
@Data
@NoArgsConstructor
public class DemonListItem implements Serializable
{
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("name")
    private String name;

    @JsonProperty("requirement")
    private Integer requirement;

    @JsonProperty("video")
    private String video;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("publisher")
    private UserDemonList publisher;

    @JsonProperty("verifier")
    private UserDemonList verifier;

    @JsonProperty("level_id")
    private Integer levelId;
}
