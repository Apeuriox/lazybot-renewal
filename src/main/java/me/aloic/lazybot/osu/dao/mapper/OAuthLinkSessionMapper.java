package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.OAuthLinkSessionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface OAuthLinkSessionMapper extends BaseMapper<OAuthLinkSessionPO>
{
    OAuthLinkSessionPO selectByStateHashForUpdate(@Param("stateHash") byte[] stateHash);

    int markConsumed(@Param("id") Long id, @Param("consumedAt") LocalDateTime consumedAt);

    int invalidateOutstanding(
            @Param("platformIdentityId") Long platformIdentityId,
            @Param("consumedAt") LocalDateTime consumedAt);
}
