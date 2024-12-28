package me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;

//PPY你真是天才，你这个数据结构和老的做成兼容的我想也没啥问题吧
@Data
@NoArgsConstructor
public class ScoreStatisticsLazer implements Serializable
{
    private Integer ok;  //100
    private Integer meh;  //50
    private Integer miss;
    private Integer good; //mania:200
    private Integer great; //std:300, ctb:fruits, mania:300
    private Integer perfect; //mania: MAX
    private Integer ignore_hit;
    private Integer ignore_miss;
    private Integer small_bonus;
    private Integer large_tick_hit;  //std:lazer only, slider  tick or reverse slider arrow /// ctb:TICKS
    private Integer slider_tail_hit; //std:lazer only, slider end
    private Integer small_tick_miss; //ctb:drp miss
    private Integer small_tick_hit; //ctb
    private Integer large_bonus;  //taiko only, idk what this is


    //我是真绷不住了，老Api如果100，50，miss是0个，他返回就是0，现在你给我说如果是0你返回的是null????
    public ScoreStatistics transformToScoreStatistics() {
        ScoreStatistics scoreStatistics = new ScoreStatistics();
        scoreStatistics.setCount_50(Optional.ofNullable(meh).orElse(0));
        scoreStatistics.setCount_100(Optional.ofNullable(ok).orElse(0));
        scoreStatistics.setCount_300(Optional.ofNullable(great).orElse(0));
        scoreStatistics.setCount_miss(Optional.ofNullable(miss).orElse(0));
        return scoreStatistics;
    }
    public void reInitialize() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(this) == null) {
                    field.set(this, 0);
                }
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("reInitialize failed in ScoreStatisticsLazer");
            }
        }
    }

}
