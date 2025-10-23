package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.BadgeDefinitionPO;
import me.aloic.lazybot.entity.po.BadgeKeyPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BadgeKeyMapper extends BaseMapper<BadgeKeyPO> {

    void insertBatch(List<BadgeKeyPO> entityList);
    BadgeKeyPO selectById(@Param("id")Integer id);
    BadgeKeyPO selectByKey(@Param("key")String key);
    void deleteById(@Param("id")Integer id);
    void updateKeyUses(BadgeKeyPO badge);

}
