package me.aloic.lazybot.osu.utils;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import java.time.ZoneId;

@Slf4j
@Component
public class PlayerStatsTableManager
{
    public static final String LOGICAL_TABLE = "player_stats_daily";
    public static final String TEMPLATE_TABLE = "player_stats_daily_template";
    public static final String META_TABLE = "player_stats_table_meta";
    public static final String WATCH_TABLE = "player_stats_watch";
    public static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private static final String STATIC_SCHEMA_RESOURCE = "sql/player_stats.sql";

    private final DataSource dataSource;

    public PlayerStatsTableManager(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public static String physicalTable(int year)
    {
        return LOGICAL_TABLE + "_" + year;
    }

    public void ensureCurrentAndNextYearsTable()
    {
        ensureStaticSchema();
        int year = Year.now(ZONE).getValue();
        ensureYear(year);
        ensureYear(year + 1);
    }

    public void ensureStaticSchema()
    {
        String script;
        ClassPathResource resource = new ClassPathResource(STATIC_SCHEMA_RESOURCE);
        if (!resource.exists()) {
            throw new LazybotRuntimeException("缺少玩家统计表结构文件: classpath:" + STATIC_SCHEMA_RESOURCE);
        }
        try (InputStream inputStream = resource.getInputStream()) {
            script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new LazybotRuntimeException("读取玩家统计表结构文件失败", e);
        }
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : script.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        }
        catch (SQLException e) {
            throw new LazybotRuntimeException("初始化玩家统计表结构失败", e);
        }
    }

    public void ensureYear(int year)
    {
        ensureStaticSchema();
        String physical = physicalTable(year);
        if (!tableExists(TEMPLATE_TABLE)) {
            throw new LazybotRuntimeException("缺少模板表 " + TEMPLATE_TABLE + "，无法创建年表 " + physical);
        }
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS `" + physical + "` LIKE `" + TEMPLATE_TABLE + "`");
            try (PreparedStatement meta = connection.prepareStatement(
                    "INSERT IGNORE INTO " + META_TABLE + " (year, table_name, created_at) VALUES (?, ?, NOW())")) {
                meta.setInt(1, year);
                meta.setString(2, physical);
                meta.executeUpdate();
            }
            log.info("Ensured player stats table {}", physical);
        }
        catch (SQLException e) {
            throw new LazybotRuntimeException("创建玩家统计年表失败: " + physical, e);
        }
    }

    public boolean existsYear(int year)
    {
        return tableExists(physicalTable(year));
    }

    private boolean tableExists(String tableName)
    {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT 1 FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?")) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            throw new LazybotRuntimeException("检查玩家统计表失败: " + tableName, e);
        }
    }

}
