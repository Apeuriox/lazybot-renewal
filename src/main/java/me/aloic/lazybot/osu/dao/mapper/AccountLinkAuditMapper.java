package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.AccountLinkAuditPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountLinkAuditMapper extends BaseMapper<AccountLinkAuditPO>
{
    int deleteByAccountId(@Param("accountId") Long accountId);
}
