package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeChallengeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BadgeChallengeMapper extends BaseMapper<BadgeChallengeDefinitionPO> {
    BadgeChallengeDefinitionPO selectById(@Param("id")Integer id);
    void deleteById(@Param("id")Integer id);

}
