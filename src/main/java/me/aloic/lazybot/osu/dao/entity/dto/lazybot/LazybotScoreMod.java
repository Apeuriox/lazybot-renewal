package me.aloic.lazybot.osu.dao.entity.dto.lazybot;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("score_mods")
@AllArgsConstructor
@NoArgsConstructor
public class LazybotScoreMod
{
    private Long scoreId;
    @TableField("mod")
    private String mod;

}
