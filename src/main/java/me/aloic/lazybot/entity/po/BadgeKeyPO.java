package me.aloic.lazybot.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.aloic.lazybot.parameter.BadgeActionParameter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "badge_key", autoResultMap = true)
public class BadgeKeyPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String cdkey;
    private Integer badge_id;
    private Integer max_uses;
    private Integer used_count;
    private Integer is_active;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expired_at;

    public BadgeKeyPO(String key, Integer badge_id, Integer max_uses, Integer expireTime)
    {
        this.cdkey=key;
        this.badge_id=badge_id;
        this.max_uses=max_uses;
        this.is_active=1;
        this.used_count=0;
        this.created_at=LocalDateTime.now();
        this.expired_at= LocalDateTime.now().plusSeconds(expireTime);
    }


}
