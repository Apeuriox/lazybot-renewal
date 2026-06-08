package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RecordEntryPemonList implements Serializable
{
    @JsonProperty("formatted_time")
    private String formattedTime;

    @JsonProperty("mobile")
    private Boolean mobile;

    @JsonProperty("player")
    private PlayerPemonList player;

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("timestamp_milliseconds")
    private Long timestampMilliseconds;

    @JsonProperty("video_id")
    private String videoId;
}
