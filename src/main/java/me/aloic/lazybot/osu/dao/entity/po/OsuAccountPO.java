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
@TableName("osu_account")
public class OsuAccountPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer lazybot_user_id;
    private String server;
    private Integer osu_user_id;
    private String username_cache;
    private String link_method;
    private LocalDateTime verified_at;
    private String avatar_etag;
    private LocalDateTime avatar_last_checked;
    private LocalDateTime avatar_next_check_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
