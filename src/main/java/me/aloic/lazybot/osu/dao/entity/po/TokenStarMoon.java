package me.aloic.lazybot.osu.dao.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenStarMoon implements Serializable {
    private Integer id;

    private Long qq_code;

    private Integer star_moon_id;

    private String star_moon_name;

    private String default_mode;
    private String default_ruleset;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;


    public String toSimpleString()
    {
        return "[Lazybot] 该用户在StarMoon的绑定情况如下\nLazybot StarMoon ID: "+id
                +"\n缓存的用户名: " + star_moon_name
                +"\nStar Moon ID: " + star_moon_id
                +"\n采用的模式: "+ default_mode
                +"\nStarMoon私服ID: " + star_moon_id
                +"\n采用的次级模式: "+ default_ruleset
                +"\n创建时间: "+ create_time;
    }
}
