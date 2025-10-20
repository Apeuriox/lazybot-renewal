package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
}
