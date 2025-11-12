package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TokenMapper extends BaseMapper<AccessTokenPO> {
    AccessTokenPO selectByQq_code(@Param("qq_code")Long qq_code);
    AccessTokenPO selectRandom();
    String selectDefaultModeByQq_code(@Param("qq_code")Long qq_code);
    void updateByToken(AccessTokenPO accessTokenPO);
    AccessTokenPO selectByPlayername(String player_name);
    AccessTokenPO selectByPlayerId(Integer player_id);
    void updateClientToken(@Param("access_token")String access_token);
    void updateDefaultMode(@Param("default_mode")String default_mode, @Param("qq_code")Long qq_code);

    void deleteByQQ(@Param("qq_code")Long qq_code);
    void updateAvatar(@Param("avatar_url")String avatar_url, @Param("player_id")Integer player_id);


    /**
     * 根据给定的qqid列表返回对应的token信息。
     * @param codes 要查询的qq账号id列表。
     * @return 对应的token信息。
     */
    List<AccessTokenPO> selectByCodes(@Param("codes") List<Long> codes);
}
