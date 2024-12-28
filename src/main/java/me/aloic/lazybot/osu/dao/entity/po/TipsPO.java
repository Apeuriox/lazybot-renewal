package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "tips", autoResultMap = true)
@Data
@NoArgsConstructor
public class TipsPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String created_by;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime last_updated;
    private String content;
    private String updated_by;
}