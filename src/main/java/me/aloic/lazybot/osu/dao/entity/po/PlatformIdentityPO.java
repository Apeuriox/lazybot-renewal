package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("platform_identity")
public class PlatformIdentityPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer lazybot_user_id;
    private String platform;
    private String platform_user_id;
    private LocalDateTime created_at;
}
