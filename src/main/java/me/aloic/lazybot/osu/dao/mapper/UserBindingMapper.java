package me.aloic.lazybot.osu.dao.mapper;

import me.aloic.lazybot.osu.dao.entity.po.UserBindingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBindingMapper
{
    UserBindingPO selectByPlatform(
            @Param("platform") String platform,
            @Param("platformUserId") String platformUserId,
            @Param("server") String server);

    UserBindingPO selectByOsuUserId(
            @Param("server") String server,
            @Param("osuUserId") Integer osuUserId);

    UserBindingPO selectByUsername(
            @Param("server") String server,
            @Param("username") String username);

    UserBindingPO selectByLazybotUserId(
            @Param("userId") Integer userId,
            @Param("server") String server);

    UserBindingPO selectRandom(
            @Param("platform") String platform,
            @Param("server") String server);

    List<UserBindingPO> selectByPlatformIds(
            @Param("platform") String platform,
            @Param("server") String server,
            @Param("platformUserIds") List<String> platformUserIds);
}
