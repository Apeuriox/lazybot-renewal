package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.AuthorityPO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AuthorityMapper extends BaseMapper<AuthorityPO> {
    AuthorityPO selectByQq(Long qq);
}
