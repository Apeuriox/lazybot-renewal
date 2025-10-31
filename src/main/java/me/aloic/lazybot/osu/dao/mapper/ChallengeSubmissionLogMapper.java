package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeChallengeSubmissionDetailsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChallengeSubmissionLogMapper extends BaseMapper<BadgeChallengeSubmissionDetailsPO> {
    BadgeChallengeSubmissionDetailsPO selectById(@Param("id")Integer id);
    void deleteById(@Param("id")Integer id);
    List<BadgeChallengeSubmissionDetailsPO> selectByPlayerIdAndChallengeId(@Param("playerId")Integer playerId,
                                                                           @Param("challengeId")Integer challengeId);
    BadgeChallengeSubmissionDetailsPO selectByPlayerIdAndStats(@Param("playerId")Integer playerId,
                                                               @Param("beatmapId")Integer beatmapId,
                                                               @Param("challengeId")Integer challengeId);

}
