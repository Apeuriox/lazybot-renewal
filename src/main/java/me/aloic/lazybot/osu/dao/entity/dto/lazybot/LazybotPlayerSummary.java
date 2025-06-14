package me.aloic.lazybot.osu.dao.entity.dto.lazybot;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("player_summary")
@AllArgsConstructor
@NoArgsConstructor
public class LazybotPlayerSummary
{
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime lastUpdated;
}
