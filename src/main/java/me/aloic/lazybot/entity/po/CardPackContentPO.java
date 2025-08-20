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
@TableName(value = "card_pack_content", autoResultMap = true)
public class CardPackContentPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer card_pack_id;
    private Integer card_id;
    private Integer weight;


}
