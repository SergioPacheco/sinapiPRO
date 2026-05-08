package com.sinapipro.api.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    private static final long MIN_DISK_SPACE_MB = 100;

    private final JdbcTemplate jdbcTemplate;

    public ApplicationHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        // Check database
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            builder.withDetail("database", "UP");
        } catch (Exception e) {
            return builder.down().withDetail("database", "DOWN: " + e.getMessage()).build();
        }

        // Check disk space
        File root = new File("/");
        long freeSpaceMb = root.getFreeSpace() / (1024 * 1024);
        builder.withDetail("diskFreeSpaceMb", freeSpaceMb);
        if (freeSpaceMb < MIN_DISK_SPACE_MB) {
            return builder.down().withDetail("disk", "LOW SPACE: " + freeSpaceMb + "MB").build();
        }

        // Check Flyway migrations applied
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM flyway_schema_history WHERE success = true", Integer.class);
            builder.withDetail("migrationsApplied", count);
        } catch (Exception e) {
            builder.withDetail("migrations", "UNKNOWN");
        }

        return builder.up().build();
    }
}
