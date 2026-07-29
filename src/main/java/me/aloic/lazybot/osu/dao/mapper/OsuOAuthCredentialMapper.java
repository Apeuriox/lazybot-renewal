package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OsuOAuthCredentialMapper extends BaseMapper<OsuOAuthCredentialPO>
{
    OsuOAuthCredentialPO selectByAccountIdForUpdate(@Param("accountId") Long accountId);

    int updateRotatedCredential(
            @Param("credential") OsuOAuthCredentialPO credential,
            @Param("expectedVersion") Long expectedVersion);
}
