package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.OsuAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface OsuAccountMapper extends BaseMapper<OsuAccountPO>
{
    OsuAccountPO selectByUserAndServer(
            @Param("userId") Integer userId,
            @Param("server") String server);

    OsuAccountPO selectByServerIdentity(
            @Param("server") String server,
            @Param("osuUserId") Integer osuUserId);

    OsuAccountPO selectByServerIdentityForUpdate(
            @Param("server") String server,
            @Param("osuUserId") Integer osuUserId);

    OsuAccountPO selectByUserAndServerForUpdate(
            @Param("userId") Integer userId,
            @Param("server") String server);

    int updateAvatarCacheMetadata(
            @Param("accountId") Long accountId,
            @Param("etag") String etag,
            @Param("checkedAt") LocalDateTime checkedAt,
            @Param("nextCheckAt") LocalDateTime nextCheckAt);

    int updateUsernameCache(
            @Param("accountId") Long accountId,
            @Param("username") String username,
            @Param("updatedAt") LocalDateTime updatedAt);
}
