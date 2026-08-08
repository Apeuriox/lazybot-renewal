package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GDDL 关卡标签数据
 */
@Data
@NoArgsConstructor
public class DemonLadderTagData implements Serializable
{
    @JsonProperty("TagID")
    private Integer tagId;

    @JsonProperty("ReactCount")
    private Integer reactCount;

    @JsonProperty("HasVoted")
    private Integer hasVoted;

    @JsonProperty("Tag")
    private DemonLadderTag tag;
}
