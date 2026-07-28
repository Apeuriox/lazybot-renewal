package me.aloic.lazybot.config;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.osu.service.LegacyIdentityMigrationService;
import me.aloic.lazybot.osu.service.LegacyIdentityMigrationService.MigrationReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(-100)
@Component
@ConditionalOnProperty(
        prefix = "lazybot.identity-migration",
        name = "enabled",
        havingValue = "true")
public class LegacyIdentityMigrationRunner implements ApplicationRunner
{
    private final LegacyIdentityMigrationService migrationService;
    private final boolean dryRun;

    public LegacyIdentityMigrationRunner(
            LegacyIdentityMigrationService migrationService,
            @Value("${lazybot.identity-migration.dry-run:true}") boolean dryRun)
    {
        this.migrationService = migrationService;
        this.dryRun = dryRun;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        MigrationReport report =
                migrationService.migrateLegacyIdentityTables(dryRun);
        log.info(
                "Legacy identity migration finished: dryRun={}, applied={}, "
                        + "usersInserted={}, usersUpdated={}, "
                        + "platformIdentitiesInserted={}, osuAccountsInserted={}, "
                        + "legacyRowsSkipped={}, warnings={}, conflicts={}",
                report.dryRun(),
                report.applied(),
                report.usersInserted(),
                report.usersUpdated(),
                report.platformIdentitiesInserted(),
                report.osuAccountsInserted(),
                report.legacyRowsSkipped(),
                report.warnings().size(),
                report.conflicts().size());

        for (String warning : report.warnings())
            log.warn("Legacy identity migration warning: {}", warning);
        for (String conflict : report.conflicts())
            log.error("Legacy identity migration conflict: {}", conflict);
    }
}
