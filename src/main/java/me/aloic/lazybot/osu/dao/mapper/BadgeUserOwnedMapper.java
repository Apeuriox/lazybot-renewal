package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeUserOwnedPO;
import me.aloic.lazybot.entity.vo.BadgeUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BadgeUserOwnedMapper extends BaseMapper<BadgeUserOwnedPO> {
    List<BadgeUserOwnedPO> selectUserBadges(@Param("user_id")Integer user_id);
    List<BadgeUserVO> selectBadgesByUserId(@Param("user_id")Integer user_id);
    BadgeUserOwnedPO selectUserSingleBadge(@Param("user_id")Integer user_id,
                                           @Param("badge_id")Integer badge_id);
    void deleteById(@Param("user_id")Integer user_id,
                    @Param("badge_id")Integer badge_id);

}
