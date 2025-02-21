package me.aloic.lazybot.osu.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aloic.lazybot.osu.dao.entity.po.ProfileCustomizationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomizationMapper extends BaseMapper<ProfileCustomizationPO> {
    ProfileCustomizationPO selectById(@Param("player_id")Integer player_id);

    List<ProfileCustomizationPO> selectUnverified();

    void updateVerified(@Param("verified")Integer verified,
                        @Param("id")Integer id);

    void updatePreferredType(@Param("preferred_type")Integer preferred_type,
                        @Param("player_id")Integer player_id);

    void updateCustomize(@Param("verified") Integer verified,
                              @Param("hue") Integer hue,
                              @Param("original_url") String original_url,
                              @Param("player_id")Integer player_id);

    void deleteByQQ_code(@Param("player_id")Integer player_id);
}
