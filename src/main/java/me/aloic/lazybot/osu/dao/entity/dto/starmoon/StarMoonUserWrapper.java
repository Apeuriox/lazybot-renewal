package me.aloic.lazybot.osu.dao.entity.dto.starmoon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StarMoonUserWrapper implements Serializable
{
    public StarMoonResult result;
    @Data
    public static class StarMoonResult
    {
        private UserResponse data;
    }
}
