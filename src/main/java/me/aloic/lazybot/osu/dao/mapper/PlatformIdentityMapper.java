package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.PlatformIdentityPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlatformIdentityMapper extends BaseMapper<PlatformIdentityPO>
{
    PlatformIdentityPO selectByPlatformIdentity(
            @Param("platform") String platform,
            @Param("platformUserId") String platformUserId);

    PlatformIdentityPO selectByIdForUpdate(@Param("id") Long id);

    int deleteByPlatformIdentity(
            @Param("platform") String platform,
            @Param("platformUserId") String platformUserId);

    int reassignToUser(
            @Param("identityId") Long identityId,
            @Param("userId") Integer userId);

    int countByUserId(@Param("userId") Integer userId);
}
