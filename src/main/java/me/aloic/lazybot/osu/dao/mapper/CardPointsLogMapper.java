package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.entity.po.CardUserPointsLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardPointsLogMapper extends BaseMapper<CardUserPointsLogPO> {
    List<CardUserPointsLogPO> selectById(@Param("user_id")Integer user_id);
    void deleteById(@Param("user_id")Integer user_id);

}
