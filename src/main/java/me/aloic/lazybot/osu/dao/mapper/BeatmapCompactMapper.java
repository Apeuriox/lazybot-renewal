package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.BeatmapCompactPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BeatmapCompactMapper extends BaseMapper<BeatmapCompactPO> {
    BeatmapCompactPO selectByBid(@Param("bid")Integer bid);
    BeatmapCompactPO selectByBidAndRuleset(@Param("bid")Integer bid, @Param("ruleset_id")Integer ruleset);
    int updateByBidAndRuleset(@Param("bid") Integer bid,
                               @Param("ruleset_id") Integer ruleset,
                               @Param("beatmap") BeatmapCompactPO beatmap);

}
