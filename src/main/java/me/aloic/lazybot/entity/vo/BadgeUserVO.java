package me.aloic.lazybot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class BadgeUserVO implements Serializable
{
    private Integer id;
    private String name;
    private String description;
    private String type;
    private String alternative_name;
    private LocalDateTime create_time;

    private LocalDateTime obtain_time;
    private Integer source_challenge_id;
}
