package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TokenMapper extends BaseMapper<AccessTokenPO> {
    AccessTokenPO selectByQq_code(@Param("qq_code")Long qq_code);
    String selectDefaultModeByQq_code(@Param("qq_code")Long qq_code);
    void updateByToken(AccessTokenPO accessTokenPO);
    AccessTokenPO selectByPlayername(String player_name);
    void updateClientToken(@Param("access_token")String access_token);
    void updateDefaultMode(@Param("default_mode")String default_mode, @Param("qq_code")Long qq_code);
    void deleteByQQ(@Param("qq_code")Long qq_code);
}
