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
@TableName("lazybot_user")
public class LazybotUserPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String default_mode;
    private String default_subset;
    private Integer preferred_panel_version;
    private Boolean enabled;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
