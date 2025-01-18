package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.UserTokenPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiscordTokenMapper extends BaseMapper<UserTokenPO> {
    UserTokenPO selectByDiscord(@Param("discord_code")Long discord_code);
    void updateByToken(UserTokenPO accessTokenPO);
    UserTokenPO selectByPlayername(String player_name);
    void updateClientToken(@Param("access_token")String access_token);
    void deleteByDiscord(@Param("discord_code")Long discord_code);
}
