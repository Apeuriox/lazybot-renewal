package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.OAuthLinkSessionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OAuthLinkSessionMapper extends BaseMapper<OAuthLinkSessionPO>
{
    @Select("""
            select *
            from oauth_link_session
            where state_hash = #{stateHash}
            limit 1
            for update
            """)
    OAuthLinkSessionPO selectByStateHashForUpdate(@Param("stateHash") byte[] stateHash);

    @Update("""
            update oauth_link_session
            set consumed_at = #{consumedAt}
            where id = #{id}
              and consumed_at is null
            """)
    int markConsumed(@Param("id") Long id, @Param("consumedAt") LocalDateTime consumedAt);

    @Update("""
            update oauth_link_session
            set consumed_at = #{consumedAt}
            where platform_identity_id = #{platformIdentityId}
              and consumed_at is null
            """)
    int invalidateOutstanding(
            @Param("platformIdentityId") Long platformIdentityId,
            @Param("consumedAt") LocalDateTime consumedAt);
}
