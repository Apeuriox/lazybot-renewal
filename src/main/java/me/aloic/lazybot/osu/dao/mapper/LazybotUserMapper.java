package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LazybotUserMapper extends BaseMapper<LazybotUserPO>
{
    int updateDefaultMode(@Param("userId") Integer userId, @Param("mode") String mode);

    int updateDefaultSubset(@Param("userId") Integer userId, @Param("subset") String subset);

    int updatePreferredPanel(
            @Param("userId") Integer userId,
            @Param("version") Integer version);
}
