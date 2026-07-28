package me.aloic.lazybot.osu.dao.mapper;

import me.aloic.lazybot.osu.dao.entity.po.AccessTokenPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @deprecated Compatibility mapper backed by the new identity tables.
 * New code should use UserBindingMapper and the individual identity mappers.
 */
@Deprecated
@Mapper
public interface TokenMapper
{
    AccessTokenPO selectByQq_code(@Param("qq_code") Long qqCode);

    AccessTokenPO selectRandom();

    AccessTokenPO selectById(@Param("id") Integer lazybotUserId);

    AccessTokenPO selectByPlayername(@Param("player_name") String playerName);

    AccessTokenPO selectByPlayerId(@Param("player_id") Integer playerId);

    List<AccessTokenPO> selectByCodes(@Param("codes") List<Long> codes);
}
