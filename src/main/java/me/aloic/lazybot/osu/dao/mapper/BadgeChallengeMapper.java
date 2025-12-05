package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeChallengeDefinitionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BadgeChallengeMapper extends BaseMapper<BadgeChallengeDefinitionPO> {
    BadgeChallengeDefinitionPO selectById(@Param("id")Integer id);
    void deleteById(@Param("id")Integer id);
    List<BadgeChallengeDefinitionPO> selectAllActive();

}
