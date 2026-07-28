package me.aloic.lazybot.osu.dao.mapper;

import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBindingMapper
{
    String BASE_SELECT = """
            select u.id,
                   p.id as platform_identity_id,
                   p.platform,
                   p.platform_user_id,
                   a.id as osu_account_id,
                   a.osu_user_id as player_id,
                   a.username_cache as player_name,
                   a.server,
                   a.link_method,
                   a.verified_at,
                   u.default_mode,
                   u.default_subset,
                   u.preferred_panel_version,
                   u.enabled,
                   a.avatar_etag,
                   a.avatar_last_checked,
                   a.avatar_next_check_at
            from lazybot_user u
            join platform_identity p on p.lazybot_user_id = u.id
            join osu_account a on a.lazybot_user_id = u.id
            """;

    @Select(BASE_SELECT + """
            where p.platform = #{platform}
              and p.platform_user_id = #{platformUserId}
              and a.server = #{server}
            limit 1
            """)
    UserBindingPO selectByPlatform(
            @Param("platform") String platform,
            @Param("platformUserId") String platformUserId,
            @Param("server") String server);

    @Select(BASE_SELECT + """
            where a.server = #{server}
              and a.osu_user_id = #{osuUserId}
            limit 1
            """)
    UserBindingPO selectByOsuUserId(
            @Param("server") String server,
            @Param("osuUserId") Integer osuUserId);

    @Select(BASE_SELECT + """
            where a.server = #{server}
              and a.username_cache = #{username}
            limit 1
            """)
    UserBindingPO selectByUsername(
            @Param("server") String server,
            @Param("username") String username);

    @Select(BASE_SELECT + """
            where p.platform = #{platform}
              and a.server = #{server}
            order by rand()
            limit 1
            """)
    UserBindingPO selectRandom(
            @Param("platform") String platform,
            @Param("server") String server);

    @Select("""
            <script>
            """ + BASE_SELECT + """
            where p.platform = #{platform}
              and a.server = #{server}
              and u.enabled = true
              and p.platform_user_id in
              <foreach collection="platformUserIds" item="id" open="(" separator="," close=")">
                  #{id}
              </foreach>
            </script>
            """)
    List<UserBindingPO> selectByPlatformIds(
            @Param("platform") String platform,
            @Param("server") String server,
            @Param("platformUserIds") List<String> platformUserIds);
}
