package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.PlayerStatisticsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PlayerStatisticsMapper extends BaseMapper<PlayerStatisticsPO>
{
    void upsertBatch(@Param("list") List<PlayerStatisticsPO> list);

    List<PlayerStatisticsPO> selectRange(@Param("id") Integer id,
                                         @Param("mode") Integer mode,
                                         @Param("subserver") Integer subserver,
                                         @Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to);
}
