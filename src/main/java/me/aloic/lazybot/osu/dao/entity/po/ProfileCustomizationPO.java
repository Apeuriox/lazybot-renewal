package me.aloic.lazybot.osu.dao.entity.po;

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
@TableName(value = "profile_customization", autoResultMap = true)
public class ProfileCustomizationPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long qq_code;

    private Long discord_code;

    private Integer player_id;

    private String player_name;

    private Integer verified;

    private Integer hue;

    private Integer preferred_type;

}
