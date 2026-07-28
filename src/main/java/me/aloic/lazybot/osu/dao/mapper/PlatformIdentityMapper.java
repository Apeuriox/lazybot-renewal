package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.PlatformIdentityPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PlatformIdentityMapper extends BaseMapper<PlatformIdentityPO>
{
    @Select("""
            select id, lazybot_user_id, platform, platform_user_id, created_at
            from platform_identity
            where platform = #{platform}
              and platform_user_id = #{platformUserId}
            limit 1
            """)
    PlatformIdentityPO selectByPlatformIdentity(
            @Param("platform") String platform,
            @Param("platformUserId") String platformUserId);

    @Select("""
            select id, lazybot_user_id, platform, platform_user_id, created_at
            from platform_identity
            where id = #{id}
            for update
            """)
    PlatformIdentityPO selectByIdForUpdate(@Param("id") Long id);

    @Delete("""
            delete from platform_identity
            where platform = #{platform}
              and platform_user_id = #{platformUserId}
            """)
    int deleteByPlatformIdentity(
            @Param("platform") String platform,
            @Param("platformUserId") String platformUserId);

    @Update("""
            update platform_identity
            set lazybot_user_id = #{userId}
            where id = #{identityId}
            """)
    int reassignToUser(
            @Param("identityId") Long identityId,
            @Param("userId") Integer userId);
}
