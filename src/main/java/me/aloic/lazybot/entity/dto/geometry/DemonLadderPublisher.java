package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GDDL 关卡的发布者信息
 */
@Data
@NoArgsConstructor
public class DemonLadderPublisher implements Serializable
{
    @JsonProperty("name")
    private String name;
}
