package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "badge_challenge_definition", autoResultMap = true)
public class BadgeChallengeDefinitionPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Integer badge_id;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expire_time;
    private Integer is_active;

    // Idk why Strung template got deleted in Java 24 so no STR there
    public String toLazybotString()
    {
        return "( ID: " + id + ")\n" + "名称: " + name + "\n" +
                "描述: " + description + "\n" +
                "开始时间: " + create_time.toLocalDate() + "\n" +
                "过期时间: " + expire_time.toLocalDate() + "\n";
    }


}
