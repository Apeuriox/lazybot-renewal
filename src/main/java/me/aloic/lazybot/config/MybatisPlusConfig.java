package me.aloic.lazybot.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import me.aloic.lazybot.osu.utils.PlayerStatsTableContext;
import me.aloic.lazybot.osu.utils.PlayerStatsTableManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig
{
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTable = new DynamicTableNameInnerInterceptor();
        dynamicTable.setTableNameHandler((sql, tableName) -> {
            if (!PlayerStatsTableManager.LOGICAL_TABLE.equals(tableName)) {
                return tableName;
            }
            Integer year = PlayerStatsTableContext.getYear();
            if (year == null) {
                throw new IllegalStateException("player_stats_daily 缺少年份路由");
            }
            return PlayerStatsTableManager.physicalTable(year);
        });
        interceptor.addInnerInterceptor(dynamicTable);
        return interceptor;
    }
}
