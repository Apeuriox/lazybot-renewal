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
@TableName("account_link_audit")
public class AccountLinkAuditPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long osu_account_id;
    private Integer previous_user_id;
    private Integer current_user_id;
    private String operation;
    private LocalDateTime created_at;
}
