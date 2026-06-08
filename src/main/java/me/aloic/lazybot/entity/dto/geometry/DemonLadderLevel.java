package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GDDL (Geometry Dash Demon Ladder) 关卡数据
 * 对应 gdladder.com API 返回的关卡信息
 */
@Data
@NoArgsConstructor
public class DemonLadderLevel implements Serializable
{
    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("Rating")
    private Double rating;

    @JsonProperty("Enjoyment")
    private Double enjoyment;

    @JsonProperty("Deviation")
    private Double deviation;

    @JsonProperty("RatingCount")
    private Integer ratingCount;

    @JsonProperty("EnjoymentCount")
    private Integer enjoymentCount;

    @JsonProperty("SubmissionCount")
    private Integer submissionCount;

    @JsonProperty("TwoPlayerRating")
    private Double twoPlayerRating;

    @JsonProperty("TwoPlayerEnjoyment")
    private Double twoPlayerEnjoyment;

    @JsonProperty("TwoPlayerDeviation")
    private Double twoPlayerDeviation;

    @JsonProperty("DefaultRating")
    private Double defaultRating;

    @JsonProperty("Showcase")
    private String showcase;

    @JsonProperty("Popularity")
    private Double popularity;

    @JsonProperty("Meta")
    private LevelMetadata meta;
}
