package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.CommandUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UsageMapper extends BaseMapper<CommandUsage> {

    CommandUsage selectById(@Param("id")Integer id);

    List<CommandUsage> selectByDate(LocalDateTime startTime, LocalDateTime endTime);

    void deleteById(@Param("id")Integer id);
}
