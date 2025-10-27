package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.parameter.BadgeActionParameter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "badge_definition", autoResultMap = true)
public class BadgeDefinitionPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private String type;
    private String alternative_name;
    private String remote_url;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;;

    public BadgeDefinitionPO(BadgeActionParameter params)
    {
        this.name=params.getName();
        this.alternative_name=params.getAltName();
        this.description=params.getDesc();
        this.type=params.getType();
        this.create_time=LocalDateTime.now();
    }
}
