package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeKeyRedeemedLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KeyRedeemedLogMapper extends BaseMapper<BadgeKeyRedeemedLogPO> {
    List<BadgeKeyRedeemedLogPO> selectByKeyId(@Param("key_id")Integer key_id);
    List<BadgeKeyRedeemedLogPO> selectByUserId(@Param("user_id")Integer user_id);
    List<BadgeKeyRedeemedLogPO> selectByBadgeId(@Param("badge_id")Integer badge_id);

    void deleteById(@Param("id")Integer id);
    void insertBatch(List<BadgeKeyRedeemedLogPO> entityList);

}
