package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * One-time migration from token / user_token_discord / token_star_moon to the
 * unified identity tables.
 *
 * <p>The migration is idempotent and never deletes legacy rows. A dry run uses
 * synthetic user ids in memory and performs no writes. In write mode any
 * detected ownership conflict aborts and rolls back the whole transaction.</p>
 */
@Service
public class LegacyIdentityMigrationService
{
    private static final String BANCHO = "bancho";
    private static final String STAR_MOON = "star_moon";
    private static final String QQ = "qq";
    private static final String DISCORD = "discord";

    private final JdbcTemplate jdbcTemplate;

    public LegacyIdentityMigrationService(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public MigrationReport migrateLegacyIdentityTables()
    {
        return migrateLegacyIdentityTables(false);
    }

    @Transactional(rollbackFor = Exception.class)
    public MigrationReport migrateLegacyIdentityTables(boolean dryRun)
    {
        requireNewTables();
        MigrationContext context = loadCurrentState(dryRun);

        List<LegacyBanchoRow> banchoRows = new ArrayList<>();
        if (tableExists("token"))
            banchoRows.addAll(loadLegacyQqRows(context));
        else
            context.warnings.add("旧表 token 不存在，已跳过 QQ Bancho 迁移");

        if (tableExists("user_token_discord"))
            banchoRows.addAll(loadLegacyDiscordRows(context));
        else
            context.warnings.add("旧表 user_token_discord 不存在，已跳过 Discord 迁移");

        migrateBanchoRows(banchoRows, context);

        if (tableExists("token_star_moon"))
            migrateStarMoonRows(loadLegacyStarMoonRows(context), context);
        else
            context.warnings.add("旧表 token_star_moon 不存在，已跳过 StarMoon 迁移");

        MigrationReport report = context.toReport();
        if (!dryRun && !report.conflicts().isEmpty()) {
            throw new LazybotRuntimeException(
                    "旧身份数据存在冲突，迁移已回滚：\n"
                            + String.join("\n", report.conflicts()));
        }
        return report;
    }

    private void migrateBanchoRows(
            List<LegacyBanchoRow> rows, MigrationContext context)
    {
        Map<Integer, List<LegacyBanchoRow>> grouped = new LinkedHashMap<>();
        for (LegacyBanchoRow row : rows)
            grouped.computeIfAbsent(row.osuUserId(), ignored -> new ArrayList<>()).add(row);

        for (Map.Entry<Integer, List<LegacyBanchoRow>> entry : grouped.entrySet())
        {
            Integer osuUserId = entry.getKey();
            List<LegacyBanchoRow> candidates = entry.getValue();

            Set<Integer> legacyQqIds = new LinkedHashSet<>();
            for (LegacyBanchoRow row : candidates) {
                if (QQ.equals(row.platform()) && row.legacyTokenId() != null)
                    legacyQqIds.add(row.legacyTokenId());
            }
            if (legacyQqIds.size() > 1) {
                context.conflicts.add(
                        "Bancho ID " + osuUserId
                                + " 对应多个旧 token.id " + legacyQqIds
                                + "；无法安全合并其徽章等关联数据");
                continue;
            }

            Set<Integer> owners = collectOwners(
                    BANCHO, osuUserId, candidates, context);
            if (owners.size() > 1) {
                context.conflicts.add(
                        "Bancho ID " + osuUserId
                                + " 在新表或旧平台身份中对应多个 lazybot_user: " + owners);
                continue;
            }

            LegacyBanchoRow primary = candidates.stream()
                    .filter(row -> QQ.equals(row.platform()))
                    .findFirst()
                    .orElse(candidates.getFirst());
            Integer userId;
            if (owners.isEmpty()) {
                Integer preferredId =
                        legacyQqIds.isEmpty() ? null : legacyQqIds.iterator().next();
                userId = createUser(
                        preferredId,
                        normalizeMode(
                                preferredDefaultMode(candidates),
                                context,
                                primary.source()),
                        "relax",
                        primary.preferredPanelVersion(),
                        primary.enabled(),
                        context);
                if (userId == null)
                    continue;
            }
            else {
                userId = owners.iterator().next();
            }

            if (!legacyQqIds.isEmpty()
                    && !Objects.equals(legacyQqIds.iterator().next(), userId)) {
                context.conflicts.add(
                        "Bancho ID " + osuUserId + " 的旧 token.id="
                                + legacyQqIds.iterator().next()
                                + "，但现有归属为 lazybot_user=" + userId
                                + "；为避免徽章等业务数据错位，已拒绝自动合并");
                continue;
            }

            AccountState userBancho =
                    context.accountsByUserServer.get(new UserServerKey(userId, BANCHO));
            if (userBancho != null && !Objects.equals(userBancho.osuUserId(), osuUserId)) {
                context.conflicts.add(
                        "lazybot_user " + userId + " 已绑定 Bancho ID "
                                + userBancho.osuUserId() + "，不能再迁移 " + osuUserId);
                continue;
            }

            boolean platformConflict = false;
            for (LegacyBanchoRow row : candidates)
            {
                PlatformKey key = new PlatformKey(row.platform(), row.platformUserId());
                Integer currentOwner = context.platformOwners.get(key);
                if (currentOwner != null && !currentOwner.equals(userId)) {
                    context.conflicts.add(
                            "平台身份 " + key + " 已属于 lazybot_user "
                                    + currentOwner + "，不能迁移到 " + userId);
                    platformConflict = true;
                }
            }
            if (platformConflict)
                continue;

            if (!context.accountsByIdentity.containsKey(
                    new AccountIdentityKey(BANCHO, osuUserId))) {
                insertAccount(
                        userId,
                        BANCHO,
                        osuUserId,
                        firstNonBlankName(candidates),
                        primary.createdAt(),
                        context);
            }

            for (LegacyBanchoRow row : candidates)
                insertPlatformIfMissing(
                        userId, row.platform(), row.platformUserId(), context);
        }
    }

    private void migrateStarMoonRows(
            List<LegacyStarMoonRow> rows, MigrationContext context)
    {
        Map<Integer, List<LegacyStarMoonRow>> grouped = new LinkedHashMap<>();
        for (LegacyStarMoonRow row : rows)
            grouped.computeIfAbsent(row.osuUserId(), ignored -> new ArrayList<>()).add(row);

        for (Map.Entry<Integer, List<LegacyStarMoonRow>> entry : grouped.entrySet())
        {
            Integer osuUserId = entry.getKey();
            List<LegacyStarMoonRow> candidates = entry.getValue();
            Set<Integer> owners = new LinkedHashSet<>();

            AccountState existing = context.accountsByIdentity.get(
                    new AccountIdentityKey(STAR_MOON, osuUserId));
            if (existing != null)
                owners.add(existing.userId());
            for (LegacyStarMoonRow row : candidates)
            {
                Integer platformOwner = context.platformOwners.get(
                        new PlatformKey(QQ, row.qqCode()));
                if (platformOwner != null)
                    owners.add(platformOwner);
            }

            if (owners.size() > 1) {
                context.conflicts.add(
                        "StarMoon ID " + osuUserId
                                + " 对应多个 lazybot_user: " + owners);
                continue;
            }

            LegacyStarMoonRow primary = candidates.getFirst();
            String subset = normalizeSubset(
                    primary.defaultSubset(), context, primary.source());
            Integer userId;
            if (owners.isEmpty()) {
                userId = createUser(
                        null,
                        normalizeMode(primary.defaultMode(), context, primary.source()),
                        subset,
                        null,
                        true,
                        context);
            }
            else {
                userId = owners.iterator().next();
                updateSubsetForNewUser(userId, subset, context);
            }
            if (userId == null)
                continue;

            AccountState userStarMoon =
                    context.accountsByUserServer.get(new UserServerKey(userId, STAR_MOON));
            if (userStarMoon != null
                    && !Objects.equals(userStarMoon.osuUserId(), osuUserId)) {
                context.conflicts.add(
                        "lazybot_user " + userId + " 已绑定 StarMoon ID "
                                + userStarMoon.osuUserId() + "，不能再迁移 " + osuUserId);
                continue;
            }

            boolean platformConflict = false;
            for (LegacyStarMoonRow row : candidates)
            {
                PlatformKey key = new PlatformKey(QQ, row.qqCode());
                Integer currentOwner = context.platformOwners.get(key);
                if (currentOwner != null && !currentOwner.equals(userId)) {
                    context.conflicts.add(
                            "QQ 身份 " + row.qqCode() + " 已属于 lazybot_user "
                                    + currentOwner + "，不能迁移到 " + userId);
                    platformConflict = true;
                }
            }
            if (platformConflict)
                continue;

            if (!context.accountsByIdentity.containsKey(
                    new AccountIdentityKey(STAR_MOON, osuUserId))) {
                insertAccount(
                        userId,
                        STAR_MOON,
                        osuUserId,
                        firstNonBlankStarMoonName(candidates),
                        primary.createdAt(),
                        context);
            }
            for (LegacyStarMoonRow row : candidates)
                insertPlatformIfMissing(userId, QQ, row.qqCode(), context);
        }
    }

    private Set<Integer> collectOwners(
            String server,
            Integer osuUserId,
            List<LegacyBanchoRow> candidates,
            MigrationContext context)
    {
        Set<Integer> owners = new LinkedHashSet<>();
        AccountState existing = context.accountsByIdentity.get(
                new AccountIdentityKey(server, osuUserId));
        if (existing != null)
            owners.add(existing.userId());
        for (LegacyBanchoRow row : candidates)
        {
            Integer platformOwner = context.platformOwners.get(
                    new PlatformKey(row.platform(), row.platformUserId()));
            if (platformOwner != null)
                owners.add(platformOwner);
        }
        return owners;
    }

    private Integer createUser(
            Integer preferredId,
            String defaultMode,
            String defaultSubset,
            Integer preferredPanelVersion,
            boolean enabled,
            MigrationContext context)
    {
        if (preferredId != null && context.userIds.contains(preferredId)) {
            context.conflicts.add(
                    "旧 token.id " + preferredId
                            + " 已被现有 lazybot_user 占用，无法保留业务关联 ID");
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        Integer userId;
        if (context.dryRun)
        {
            userId = preferredId != null ? preferredId : context.nextSyntheticUserId--;
        }
        else if (preferredId != null)
        {
            jdbcTemplate.update("""
                    insert into lazybot_user
                        (id, default_mode, default_subset, preferred_panel_version,
                         enabled, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?)
                    """,
                    preferredId,
                    defaultMode,
                    defaultSubset,
                    preferredPanelVersion,
                    enabled,
                    now,
                    now);
            userId = preferredId;
        }
        else
        {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("""
                        insert into lazybot_user
                            (default_mode, default_subset, preferred_panel_version,
                             enabled, created_at, updated_at)
                        values (?, ?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, defaultMode);
                statement.setString(2, defaultSubset);
                if (preferredPanelVersion == null)
                    statement.setObject(3, null);
                else
                    statement.setInt(3, preferredPanelVersion);
                statement.setBoolean(4, enabled);
                statement.setObject(5, now);
                statement.setObject(6, now);
                return statement;
            }, keyHolder);
            Number generatedKey = keyHolder.getKey();
            if (generatedKey == null)
                throw new LazybotRuntimeException("创建 lazybot_user 后未获得主键");
            userId = generatedKey.intValue();
        }

        context.userIds.add(userId);
        context.createdUserIds.add(userId);
        context.usersInserted++;
        return userId;
    }

    private void insertPlatformIfMissing(
            Integer userId,
            String platform,
            String platformUserId,
            MigrationContext context)
    {
        PlatformKey key = new PlatformKey(platform, platformUserId);
        if (context.platformOwners.containsKey(key))
            return;

        if (!context.dryRun) {
            jdbcTemplate.update("""
                    insert into platform_identity
                        (lazybot_user_id, platform, platform_user_id, created_at)
                    values (?, ?, ?, ?)
                    """,
                    userId, platform, platformUserId, LocalDateTime.now());
        }
        context.platformOwners.put(key, userId);
        context.platformIdentitiesInserted++;
    }

    private void insertAccount(
            Integer userId,
            String server,
            Integer osuUserId,
            String username,
            LocalDateTime legacyCreatedAt,
            MigrationContext context)
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = legacyCreatedAt == null ? now : legacyCreatedAt;
        if (!context.dryRun) {
            jdbcTemplate.update("""
                    insert into osu_account
                        (lazybot_user_id, server, osu_user_id, username_cache,
                         link_method, verified_at, created_at, updated_at)
                    values (?, ?, ?, ?, 'manual', null, ?, ?)
                    """,
                    userId, server, osuUserId, username, createdAt, now);
        }

        AccountState account = new AccountState(userId, server, osuUserId);
        context.accountsByIdentity.put(
                new AccountIdentityKey(server, osuUserId), account);
        context.accountsByUserServer.put(
                new UserServerKey(userId, server), account);
        context.osuAccountsInserted++;
    }

    private void updateSubsetForNewUser(
            Integer userId, String subset, MigrationContext context)
    {
        if (!context.createdUserIds.contains(userId)
                || !context.subsetUpdatedUsers.add(userId))
            return;
        if (!context.dryRun) {
            jdbcTemplate.update("""
                    update lazybot_user
                    set default_subset = ?,
                        updated_at = ?
                    where id = ?
                    """, subset, LocalDateTime.now(), userId);
        }
        context.usersUpdated++;
    }

    private MigrationContext loadCurrentState(boolean dryRun)
    {
        MigrationContext context = new MigrationContext(dryRun);
        for (Map<String, Object> row :
                jdbcTemplate.queryForList("select id from lazybot_user"))
            context.userIds.add(asInt(row.get("id")));

        for (Map<String, Object> row : jdbcTemplate.queryForList("""
                select lazybot_user_id, platform, platform_user_id
                from platform_identity
                """))
        {
            context.platformOwners.put(
                    new PlatformKey(
                            asString(row.get("platform")).toLowerCase(Locale.ROOT),
                            asString(row.get("platform_user_id"))),
                    asInt(row.get("lazybot_user_id")));
        }

        for (Map<String, Object> row : jdbcTemplate.queryForList("""
                select lazybot_user_id, server, osu_user_id
                from osu_account
                """))
        {
            AccountState account = new AccountState(
                    asInt(row.get("lazybot_user_id")),
                    asString(row.get("server")).toLowerCase(Locale.ROOT),
                    asInt(row.get("osu_user_id")));
            context.accountsByIdentity.put(
                    new AccountIdentityKey(account.server(), account.osuUserId()),
                    account);
            context.accountsByUserServer.put(
                    new UserServerKey(account.userId(), account.server()),
                    account);
        }
        return context;
    }

    private List<LegacyBanchoRow> loadLegacyQqRows(MigrationContext context)
    {
        List<LegacyBanchoRow> rows = new ArrayList<>();
        String preferredPanelColumn = columnExists(
                "token", "preferred_panel_version")
                ? "preferred_panel_version"
                : "null as preferred_panel_version";
        String sql = """
                select id, qq_code, player_id, player_name, default_mode,
                       valid, %s
                from `token`
                order by id
                """.formatted(preferredPanelColumn);
        for (Map<String, Object> row : jdbcTemplate.queryForList(sql))
        {
            Long qqCode = asLongOrNull(row.get("qq_code"));
            Integer playerId = asIntOrNull(row.get("player_id"));
            if (qqCode == null || qqCode == 0L || playerId == null || playerId <= 0) {
                context.legacyRowsSkipped++;
                continue;
            }
            rows.add(new LegacyBanchoRow(
                    "token#" + row.get("id"),
                    QQ,
                    String.valueOf(qqCode),
                    playerId,
                    asStringOrNull(row.get("player_name")),
                    row.get("default_mode"),
                    asIntOrNull(row.get("id")),
                    asIntOrNull(row.get("preferred_panel_version")),
                    enabled(row.get("valid")),
                    null));
        }
        return rows;
    }

    private List<LegacyBanchoRow> loadLegacyDiscordRows(MigrationContext context)
    {
        List<LegacyBanchoRow> rows = new ArrayList<>();
        for (Map<String, Object> row : jdbcTemplate.queryForList("""
                select id, discord_code, player_id, player_name, default_mode
                from user_token_discord
                order by id
                """))
        {
            Long discordCode = asLongOrNull(row.get("discord_code"));
            Integer playerId = asIntOrNull(row.get("player_id"));
            if (discordCode == null || discordCode == 0L
                    || playerId == null || playerId <= 0) {
                context.legacyRowsSkipped++;
                continue;
            }
            rows.add(new LegacyBanchoRow(
                    "user_token_discord#" + row.get("id"),
                    DISCORD,
                    String.valueOf(discordCode),
                    playerId,
                    asStringOrNull(row.get("player_name")),
                    row.get("default_mode"),
                    null,
                    null,
                    true,
                    null));
        }
        return rows;
    }

    private List<LegacyStarMoonRow> loadLegacyStarMoonRows(MigrationContext context)
    {
        List<LegacyStarMoonRow> rows = new ArrayList<>();
        for (Map<String, Object> row : jdbcTemplate.queryForList("""
                select id, star_moon_id, star_moon_name, qq_code,
                       create_time, default_mode, default_ruleset
                from token_star_moon
                order by id
                """))
        {
            Long qqCode = asLongOrNull(row.get("qq_code"));
            Integer playerId = asIntOrNull(row.get("star_moon_id"));
            if (qqCode == null || qqCode == 0L || playerId == null || playerId <= 0) {
                context.legacyRowsSkipped++;
                continue;
            }
            rows.add(new LegacyStarMoonRow(
                    "token_star_moon#" + row.get("id"),
                    String.valueOf(qqCode),
                    playerId,
                    asStringOrNull(row.get("star_moon_name")),
                    row.get("default_mode"),
                    asStringOrNull(row.get("default_ruleset")),
                    asDateTimeOrNull(row.get("create_time"))));
        }
        return rows;
    }

    private void requireNewTables()
    {
        for (String table : List.of(
                "lazybot_user", "platform_identity", "osu_account")) {
            if (!tableExists(table))
                throw new LazybotRuntimeException("迁移所需的新表不存在: " + table);
        }
    }

    private boolean tableExists(String tableName)
    {
        Boolean exists = jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(
                    connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    if (tableName.equalsIgnoreCase(tables.getString("TABLE_NAME")))
                        return true;
                }
                return false;
            }
        });
        return Boolean.TRUE.equals(exists);
    }

    private boolean columnExists(String tableName, String columnName)
    {
        Boolean exists = jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet columns = metadata.getColumns(
                    connection.getCatalog(), null, "%", "%")) {
                while (columns.next()) {
                    if (tableName.equalsIgnoreCase(columns.getString("TABLE_NAME"))
                            && columnName.equalsIgnoreCase(
                            columns.getString("COLUMN_NAME")))
                        return true;
                }
                return false;
            }
        });
        return Boolean.TRUE.equals(exists);
    }

    private static String firstNonBlankName(List<LegacyBanchoRow> rows)
    {
        return rows.stream()
                .map(LegacyBanchoRow::username)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse("unknown");
    }

    private static Object preferredDefaultMode(List<LegacyBanchoRow> rows)
    {
        return rows.stream()
                .filter(row -> QQ.equals(row.platform()) && row.defaultMode() != null)
                .map(LegacyBanchoRow::defaultMode)
                .findFirst()
                .orElseGet(() -> rows.stream()
                        .map(LegacyBanchoRow::defaultMode)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null));
    }

    private static String firstNonBlankStarMoonName(List<LegacyStarMoonRow> rows)
    {
        return rows.stream()
                .map(LegacyStarMoonRow::username)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse("unknown");
    }

    private static String normalizeMode(
            Object raw, MigrationContext context, String source)
    {
        if (raw == null)
            return "osu";
        String value = String.valueOf(raw).trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "0", "osu", "std", "standard" -> "osu";
            case "1", "taiko" -> "taiko";
            case "2", "fruits", "catch", "ctb" -> "fruits";
            case "3", "mania" -> "mania";
            default -> {
                context.warnings.add(
                        source + " 的 default_mode=" + raw + " 无法识别，已使用 osu");
                yield "osu";
            }
        };
    }

    private static String normalizeSubset(
            String raw, MigrationContext context, String source)
    {
        if (raw == null || raw.isBlank())
            return "relax";
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "std", "s", "0", "standard" -> "standard";
            case "relax", "rx", "1", "rl" -> "relax";
            case "ap", "auto", "2", "autopilot" -> "autopilot";
            default -> {
                context.warnings.add(
                        source + " 的 default_ruleset=" + raw
                                + " 无法识别，已使用 relax");
                yield "relax";
            }
        };
    }

    private static boolean enabled(Object raw)
    {
        if (raw == null)
            return true;
        if (raw instanceof Boolean value)
            return value;
        if (raw instanceof Number value)
            return value.intValue() != 0;
        return !Set.of("0", "false", "no").contains(
                String.valueOf(raw).trim().toLowerCase(Locale.ROOT));
    }

    private static String asString(Object value)
    {
        return String.valueOf(value);
    }

    private static String asStringOrNull(Object value)
    {
        return value == null ? null : String.valueOf(value);
    }

    private static Integer asInt(Object value)
    {
        Integer result = asIntOrNull(value);
        if (result == null)
            throw new LazybotRuntimeException("数据库整数值为空");
        return result;
    }

    private static Integer asIntOrNull(Object value)
    {
        if (value == null)
            return null;
        try
        {
            long parsed = value instanceof Number number
                    ? number.longValue()
                    : Long.parseLong(String.valueOf(value).trim());
            if (parsed < Integer.MIN_VALUE || parsed > Integer.MAX_VALUE)
                return null;
            return (int) parsed;
        }
        catch (NumberFormatException ignored)
        {
            return null;
        }
    }

    private static Long asLongOrNull(Object value)
    {
        if (value == null)
            return null;
        try {
            return value instanceof Number number
                    ? number.longValue()
                    : Long.parseLong(String.valueOf(value).trim());
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static LocalDateTime asDateTimeOrNull(Object value)
    {
        if (value == null)
            return null;
        if (value instanceof LocalDateTime dateTime)
            return dateTime;
        if (value instanceof java.sql.Timestamp timestamp)
            return timestamp.toLocalDateTime();
        return null;
    }

    public record MigrationReport(
            boolean dryRun,
            boolean applied,
            int usersInserted,
            int usersUpdated,
            int platformIdentitiesInserted,
            int osuAccountsInserted,
            int legacyRowsSkipped,
            List<String> warnings,
            List<String> conflicts)
    {
    }

    private static final class MigrationContext
    {
        private final boolean dryRun;
        private final Set<Integer> userIds = new HashSet<>();
        private final Set<Integer> createdUserIds = new HashSet<>();
        private final Set<Integer> subsetUpdatedUsers = new HashSet<>();
        private final Map<PlatformKey, Integer> platformOwners = new HashMap<>();
        private final Map<AccountIdentityKey, AccountState> accountsByIdentity =
                new HashMap<>();
        private final Map<UserServerKey, AccountState> accountsByUserServer =
                new HashMap<>();
        private final List<String> warnings = new ArrayList<>();
        private final List<String> conflicts = new ArrayList<>();
        private int nextSyntheticUserId = -1;
        private int usersInserted;
        private int usersUpdated;
        private int platformIdentitiesInserted;
        private int osuAccountsInserted;
        private int legacyRowsSkipped;

        private MigrationContext(boolean dryRun)
        {
            this.dryRun = dryRun;
        }

        private MigrationReport toReport()
        {
            return new MigrationReport(
                    dryRun,
                    !dryRun && conflicts.isEmpty(),
                    usersInserted,
                    usersUpdated,
                    platformIdentitiesInserted,
                    osuAccountsInserted,
                    legacyRowsSkipped,
                    List.copyOf(warnings),
                    List.copyOf(conflicts));
        }
    }

    private record PlatformKey(String platform, String platformUserId)
    {
    }

    private record AccountIdentityKey(String server, Integer osuUserId)
    {
    }

    private record UserServerKey(Integer userId, String server)
    {
    }

    private record AccountState(Integer userId, String server, Integer osuUserId)
    {
    }

    private record LegacyBanchoRow(
            String source,
            String platform,
            String platformUserId,
            Integer osuUserId,
            String username,
            Object defaultMode,
            Integer legacyTokenId,
            Integer preferredPanelVersion,
            boolean enabled,
            LocalDateTime createdAt)
    {
    }

    private record LegacyStarMoonRow(
            String source,
            String qqCode,
            Integer osuUserId,
            String username,
            Object defaultMode,
            String defaultSubset,
            LocalDateTime createdAt)
    {
    }
}
