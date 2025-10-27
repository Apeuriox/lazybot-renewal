package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeUserShowcasePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BadgeShowcaseMapper extends BaseMapper<BadgeUserShowcasePO> {
    List<BadgeUserShowcasePO> selectByUserId(@Param("id")Integer id);
    void deleteByBadgeIdAndUserId(@Param("badge_id")Integer badge_id,
                                  @Param("lazybot_id")Integer lazybot_id);
    void deleteAllUser(@Param("lazybot_id")Integer lazybot_id);
    void insertBatch(List<BadgeUserShowcasePO> badges);

}
