package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GDDL 关卡的音乐信息
 */
@Data
@NoArgsConstructor
public class DemonLadderSong implements Serializable
{
    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Author")
    private String author;

    @JsonProperty("Size")
    private String size;
}
