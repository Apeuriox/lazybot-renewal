package me.aloic.lazybot.osu.dao.entity.po;

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
@TableName(value = "profile_customization", autoResultMap = true)
public class ProfileCustomizationPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long qq_code;

    private String original_url;

    private Integer player_id;

    private String player_name;

    private Integer verified;

    private Integer hue;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime last_updated;

    private Integer preferred_type;

}
