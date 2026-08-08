package me.aloic.lazybot.entity.dto.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GD关卡的完整数据包，包含关卡信息、作者、音乐和分页信息
 * 对应TS中的 GdLevelData 接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemonLadderLevelData implements Serializable
{
    private LevelRaw level;
    private Creator creator;
    private LevelSong song;
    private SearchPagination pageInfo;
}
