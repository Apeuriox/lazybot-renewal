package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TokenMapper extends BaseMapper<UserTokenPO> {
    UserTokenPO selectByQQ(@Param("qq_code")Long qq_code);
    UserTokenPO selectByDiscord(@Param("discord_code")Long discord_code);
    String selectDefaultModeByQq_code(@Param("qq_code")Long qq_code);
    void updateByToken(UserTokenPO accessTokenPO);
    UserTokenPO selectByPlayername(String player_name);
    void updateClientToken(@Param("access_token")String access_token);
    void deleteByQQ(@Param("qq_code")Long qq_code);
    void deleteByDiscord(@Param("discord_code")Long discord_code);
}
