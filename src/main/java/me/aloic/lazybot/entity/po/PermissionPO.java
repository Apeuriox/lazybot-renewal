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
@TableName(value = "permission", autoResultMap = true)
public class PermissionPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String target_type;
    private Long target_id;

    private String command;
    private Integer version;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;

    public PermissionPO(String target_type, Long target_id, String command, Integer version)
    {
        this.target_type = target_type;
        this.target_id = target_id;
        this.command = command;
        this.version = version;
        this.created_at=LocalDateTime.now();
    }
}
