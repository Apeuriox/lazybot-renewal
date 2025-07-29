package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "command_usage", autoResultMap = true)
public class CommandUsage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer total;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<LazybotUsageTimeDistribution> distribution;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<LazybotUsageSource> source;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<LazybotUsageCommand> command;

    private Integer is_complete;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;

    public CommandUsage(Integer total,
                        List<LazybotUsageTimeDistribution> distribution,
                        List<LazybotUsageSource> source,
                        List<LazybotUsageCommand> command,
                        Integer is_complete,
                        LocalDateTime created_at) {
        this.total = total;
        this.distribution = distribution;
        this.source = source;
        this.command = command;
        this.is_complete = is_complete;
        this.created_at = created_at;

    }

}
