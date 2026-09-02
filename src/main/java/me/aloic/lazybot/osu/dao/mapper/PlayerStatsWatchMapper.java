package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatsSnapshotTarget;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatsWatchPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerStatsWatchMapper extends BaseMapper<PlayerStatsWatchPO>
{
    void upsert(PlayerStatsWatchPO watch);

    void upsertBatch(@Param("list") List<PlayerStatsWatchPO> list);

    List<PlayerStatsSnapshotTarget> selectActiveSnapshotTargets(@Param("subserver") int subserver);
}
