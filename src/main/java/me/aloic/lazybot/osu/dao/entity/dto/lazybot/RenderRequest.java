package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenderRequest<T>
{
    private T data;
    private Integer width;
    private Integer height;
}
