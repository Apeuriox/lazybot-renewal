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
@TableName("oauth_link_session")
public class OAuthLinkSessionPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Long id;

    private byte[] state_hash;
    private Long platform_identity_id;
    private LocalDateTime expires_at;
    private LocalDateTime consumed_at;
}
