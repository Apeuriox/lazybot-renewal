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

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "badge_user_showcase", autoResultMap = true)
public class BadgeUserShowcasePO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer badge_id;
    private Integer lazybot_id;

    public BadgeUserShowcasePO(Integer badge_id,Integer lazybot_id)
    {
        this.badge_id=badge_id;
        this.lazybot_id=lazybot_id;
    }


}
