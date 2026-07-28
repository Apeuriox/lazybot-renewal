package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.LazybotUserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LazybotUserMapper extends BaseMapper<LazybotUserPO>
{
    @Update("""
            update lazybot_user
            set default_mode = #{mode}
            where id = #{userId}
            """)
    int updateDefaultMode(@Param("userId") Integer userId, @Param("mode") String mode);

    @Update("""
            update lazybot_user
            set default_subset = #{subset}
            where id = #{userId}
            """)
    int updateDefaultSubset(@Param("userId") Integer userId, @Param("subset") String subset);

    @Update("""
            update lazybot_user
            set preferred_panel_version = #{version}
            where id = #{userId}
            """)
    int updatePreferredPanel(
            @Param("userId") Integer userId,
            @Param("version") Integer version);
}
