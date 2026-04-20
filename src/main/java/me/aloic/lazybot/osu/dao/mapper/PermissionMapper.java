package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionPO>
{
    PermissionPO selectById(@Param("id")Integer id);

    List<PermissionPO> selectAll();

    PermissionPO selectByStats(@Param("target_type")String target_type,
                                     @Param("target_id")Long target_id,
                                     @Param("command")String command,
                                     @Param("version")Integer version);

    void deleteByStats(@Param("target_id")Long target_id,
                        @Param("command")String command,
                       @Param("version")Integer version);

    void deleteByUserId(@Param("target_id")Long target_id);
}
