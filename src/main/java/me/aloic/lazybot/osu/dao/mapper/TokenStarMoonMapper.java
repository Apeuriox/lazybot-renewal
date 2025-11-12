package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.TokenStarMoon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface TokenStarMoonMapper extends BaseMapper<TokenStarMoon> {
    TokenStarMoon selectByQq_code(@Param("qq_code")Long qq_code);
    TokenStarMoon selectRandom();
    String selectDefaultModeByQq_code(@Param("qq_code")Long qq_code);
    void updateByToken(TokenStarMoon token);
    TokenStarMoon selectByPlayername(String player_name);
    TokenStarMoon selectByPlayerId(Integer player_id);

    void deleteByQQ(@Param("qq_code")Long qq_code);

}
