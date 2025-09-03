package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.CardUserPointsPO;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CardPointsMapper extends BaseMapper<CardUserPointsPO> {
    CardUserPointsPO selectById(@Param("user_id")Integer user_id);


    void updateStatsCheck(@Param("points") Integer points,
                              @Param("last_signin_time") LocalDateTime last_signin_time,
                              @Param("total_history_points") Integer total_history_points,
                              @Param("accumulated_check_time") Integer accumulated_check_time,
                              @Param("continuous_check_time") Integer continuous_check_time,
                              @Param("user_id")Integer user_id);
    void updateStatsConsume(@Param("points") Integer points,
                     @Param("total_spent_points") Integer total_spent_points,
                     @Param("user_id")Integer user_id);

    void deleteById(@Param("user_id")Integer user_id);
}
