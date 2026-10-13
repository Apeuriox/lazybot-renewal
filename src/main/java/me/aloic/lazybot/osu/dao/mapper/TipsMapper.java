package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.TipsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TipsMapper extends BaseMapper<TipsPO> {
    TipsPO selectById(@Param("id")Long id);
    List<TipsPO> selectByKeywords(@Param("keywords") List<String> keywords);
    TipsPO selectRandom();
    void updateTips(TipsPO tips);
    TipsPO selectByCreator(String creator);
}
