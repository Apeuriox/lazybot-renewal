package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomizationMapper extends BaseMapper<ProfileCustomizationPO> {
    ProfileCustomizationPO selectById(@Param("player_id")Integer player_id);

    void updateVerified(@Param("verified")Integer verified,
                        @Param("player_id")Integer player_id);
    void updateVerifiedAndHue(@Param("verified") Integer verified,
                              @Param("hue") Integer hue,
                              @Param("player_id")Integer player_id);
    void deleteByQQ_code(@Param("player_id")Integer player_id);
}
