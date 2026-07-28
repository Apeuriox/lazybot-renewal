package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.AccountLinkAuditPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountLinkAuditMapper extends BaseMapper<AccountLinkAuditPO>
{
    @Delete("""
            delete from account_link_audit
            where osu_account_id = #{accountId}
            """)
    int deleteByAccountId(@Param("accountId") Long accountId);
}
