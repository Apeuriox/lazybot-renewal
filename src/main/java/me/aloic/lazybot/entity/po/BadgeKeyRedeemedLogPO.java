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
@TableName(value = "badge_key_redeem_log", autoResultMap = true)
public class BadgeKeyRedeemedLogPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer key_id;
    private Integer badge_id;
    private Integer user_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime redeemed_at;

    public BadgeKeyRedeemedLogPO(Integer key_id, Integer badge_id, Integer user_id)
    {
        this.key_id = key_id;
        this.badge_id = badge_id;
        this.user_id = user_id;
        this.redeemed_at = LocalDateTime.now();
    }



}
