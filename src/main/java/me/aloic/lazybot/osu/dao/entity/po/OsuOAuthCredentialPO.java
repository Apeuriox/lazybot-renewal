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
@TableName("osu_oauth_credential")
public class OsuOAuthCredentialPO implements Serializable
{
    @TableId(type = IdType.INPUT)
    private Long osu_account_id;

    private String access_token;
    private String refresh_token;
    private LocalDateTime access_token_expires_at;
    private String granted_scopes;
    private Long row_version;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
