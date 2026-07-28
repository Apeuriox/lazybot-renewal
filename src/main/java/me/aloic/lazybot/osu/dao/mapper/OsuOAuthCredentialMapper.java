package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.OsuOAuthCredentialPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OsuOAuthCredentialMapper extends BaseMapper<OsuOAuthCredentialPO>
{
    @Select("""
            select *
            from osu_oauth_credential
            where osu_account_id = #{accountId}
            for update
            """)
    OsuOAuthCredentialPO selectByAccountIdForUpdate(@Param("accountId") Long accountId);

    @Update("""
            update osu_oauth_credential
            set access_token = #{credential.access_token},
                refresh_token = #{credential.refresh_token},
                access_token_expires_at = #{credential.access_token_expires_at},
                granted_scopes = #{credential.granted_scopes},
                row_version = row_version + 1,
                updated_at = #{credential.updated_at}
            where osu_account_id = #{credential.osu_account_id}
              and row_version = #{expectedVersion}
            """)
    int updateRotatedCredential(
            @Param("credential") OsuOAuthCredentialPO credential,
            @Param("expectedVersion") Long expectedVersion);
}
