package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PlayerPemonList implements Serializable
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;
}
