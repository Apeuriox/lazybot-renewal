package me.aloic.lazybot.entity.dto.geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * GDDL 搜索结果
 */
@Data
@NoArgsConstructor
public class DemonLadderSearchData implements Serializable
{
    @JsonProperty("total")
    private Integer total;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("levels")
    private List<DemonLadderLevel> levels;
}
