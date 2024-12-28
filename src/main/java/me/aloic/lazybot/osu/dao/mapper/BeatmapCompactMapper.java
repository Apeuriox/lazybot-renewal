package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.BeatmapCompactPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BeatmapCompactMapper extends BaseMapper<BeatmapCompactPO> {
    BeatmapCompactPO selectByBid(@Param("bid")Integer bid);
}
