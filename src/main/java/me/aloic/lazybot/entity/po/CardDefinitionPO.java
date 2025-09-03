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
@TableName(value = "card_definition", autoResultMap = true)
public class CardDefinitionPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer osu_id;
    private String name;
    private String rarity;
    private String description;
    private String type;
    private String image_url;

}
