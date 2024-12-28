package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.BeatmapPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BeatmapMapper extends BaseMapper<BeatmapPO> {
    BeatmapPO selectByBid(@Param("bid")Integer bid);
}
