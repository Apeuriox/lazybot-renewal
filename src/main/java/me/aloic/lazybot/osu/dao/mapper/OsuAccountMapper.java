package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.OsuAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OsuAccountMapper extends BaseMapper<OsuAccountPO>
{
    @Select("""
            select *
            from osu_account
            where lazybot_user_id = #{userId}
              and server = #{server}
            limit 1
            """)
    OsuAccountPO selectByUserAndServer(
            @Param("userId") Integer userId,
            @Param("server") String server);

    @Select("""
            select *
            from osu_account
            where server = #{server}
              and osu_user_id = #{osuUserId}
            limit 1
            """)
    OsuAccountPO selectByServerIdentity(
            @Param("server") String server,
            @Param("osuUserId") Integer osuUserId);

    @Select("""
            select *
            from osu_account
            where server = #{server}
              and osu_user_id = #{osuUserId}
            limit 1
            for update
            """)
    OsuAccountPO selectByServerIdentityForUpdate(
            @Param("server") String server,
            @Param("osuUserId") Integer osuUserId);

    @Select("""
            select *
            from osu_account
            where lazybot_user_id = #{userId}
              and server = #{server}
            limit 1
            for update
            """)
    OsuAccountPO selectByUserAndServerForUpdate(
            @Param("userId") Integer userId,
            @Param("server") String server);

    @Update("""
            update osu_account
            set avatar_etag = #{etag},
                avatar_last_checked = #{checkedAt},
                avatar_next_check_at = #{nextCheckAt},
                updated_at = #{checkedAt}
            where id = #{accountId}
            """)
    int updateAvatarCacheMetadata(
            @Param("accountId") Long accountId,
            @Param("etag") String etag,
            @Param("checkedAt") LocalDateTime checkedAt,
            @Param("nextCheckAt") LocalDateTime nextCheckAt);

    @Update("""
            update osu_account
            set username_cache = #{username},
                updated_at = #{updatedAt}
            where id = #{accountId}
            """)
    int updateUsernameCache(
            @Param("accountId") Long accountId,
            @Param("username") String username,
            @Param("updatedAt") LocalDateTime updatedAt);
}
