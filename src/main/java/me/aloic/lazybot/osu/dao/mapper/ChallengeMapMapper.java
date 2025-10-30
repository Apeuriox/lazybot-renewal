package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeChallengeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeChallengeMapPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChallengeMapMapper extends BaseMapper<BadgeChallengeMapPO> {
    BadgeChallengeMapPO selectById(@Param("id")Integer id);
    void deleteById(@Param("id")Integer id);
    BadgeChallengeMapPO selectByBeatmapIdAndChallengeId(@Param("beatmapId")Integer beatmapId,
                                                        @Param("challengeId")Integer challengeId);

}
