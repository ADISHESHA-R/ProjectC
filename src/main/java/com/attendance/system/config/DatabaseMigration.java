package com.attendance.system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Best-effort schema alignment for existing deployments. DDL is split by engine so PostgreSQL (Render)
 * and MySQL (client-hosted) stay supported; H2 local skips this (Hibernate {@code ddl-auto} is enough).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigration {

    private enum DbKind {
        H2,
        MYSQL,
        POSTGRESQL
    }

    private final JdbcTemplate jdbcTemplate;

    private DbKind dbKind = DbKind.POSTGRESQL;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void onApplicationReady() {
        try {
            this.dbKind = detectDbKind();
            log.info("Database migration: detected engine = {}", dbKind);
            if (dbKind == DbKind.H2) {
                log.info("Skipping legacy JDBC migrations on H2 (local dev uses Hibernate ddl-auto)");
                return;
            }
            Thread.sleep(1000);
            log.info("Starting database migration...");
            migrateUsersTable();
            migrateSitesTable();
            migrateJobSiteFlexibleRows();
            log.info("Database migration completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Migration interrupted");
        } catch (Exception e) {
            log.error("Database migration failed: {}", e.getMessage(), e);
            log.warn("Application will continue despite migration errors");
        }
    }

    private DbKind detectDbKind() {
        try {
            return jdbcTemplate.execute((ConnectionCallback<DbKind>) con -> {
                String p = con.getMetaData().getDatabaseProductName();
                if (p == null) {
                    return DbKind.POSTGRESQL;
                }
                String pl = p.toLowerCase(Locale.ROOT);
                if (pl.contains("mysql")) {
                    return DbKind.MYSQL;
                }
                if (pl.contains("h2")) {
                    return DbKind.H2;
                }
                return DbKind.POSTGRESQL;
            });
        } catch (Exception e) {
            log.warn("Could not detect database product, assuming PostgreSQL: {}", e.getMessage());
            return DbKind.POSTGRESQL;
        }
    }

    private boolean tableExists(String tableName) {
        if (dbKind == DbKind.MYSQL) {
            Integer c = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND LOWER(table_name) = LOWER(?)",
                Integer.class,
                tableName);
            return c != null && c > 0;
        }
        Integer c = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE LOWER(table_name) = LOWER(?)",
            Integer.class,
            tableName);
        return c != null && c > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        if (dbKind == DbKind.MYSQL) {
            return !jdbcTemplate.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_schema = DATABASE() "
                    + "AND LOWER(table_name) = LOWER(?) AND LOWER(column_name) = LOWER(?)",
                tableName,
                columnName).isEmpty();
        }
        return !jdbcTemplate.queryForList(
            "SELECT column_name FROM information_schema.columns WHERE LOWER(table_name) = LOWER(?) "
                + "AND LOWER(column_name) = LOWER(?)",
            tableName,
            columnName).isEmpty();
    }

    private void migrateUsersTable() {
        try {
            if (!tableExists("users")) {
                log.info("Users table does not exist yet. Hibernate will create it.");
                return;
            }
            if (!columnExists("users", "employee_id")) {
                log.info("Adding missing column: users.employee_id");
                String altSql = dbKind == DbKind.MYSQL
                    ? """
                    SELECT column_name FROM information_schema.columns
                    WHERE table_schema = DATABASE() AND table_name = 'users'
                    AND (column_name LIKE '%employee%' OR column_name LIKE '%Employee%')
                    """
                    : """
                    SELECT column_name FROM information_schema.columns
                    WHERE table_name = 'users'
                    AND (column_name LIKE '%employee%' OR column_name LIKE '%Employee%')
                    """;
                List<Map<String, Object>> altResults = jdbcTemplate.queryForList(altSql);
                if (!altResults.isEmpty()) {
                    String existingColumn = (String) altResults.get(0).get("column_name");
                    log.info("Found existing column: {}. Renaming to employee_id", existingColumn);
                    renameColumn("users", existingColumn, "employee_id");
                } else {
                    jdbcTemplate.execute("""
                        ALTER TABLE users
                        ADD COLUMN employee_id VARCHAR(50) UNIQUE
                        """);
                    if (dbKind == DbKind.MYSQL) {
                        jdbcTemplate.execute("""
                            UPDATE users
                            SET employee_id = CONCAT('EMP', LPAD(CAST(id AS CHAR), 6, '0'))
                            WHERE employee_id IS NULL
                            """);
                        jdbcTemplate.execute("ALTER TABLE users MODIFY employee_id VARCHAR(50) NOT NULL");
                    } else {
                        jdbcTemplate.execute("""
                            UPDATE users
                            SET employee_id = 'EMP' || LPAD(id::text, 6, '0')
                            WHERE employee_id IS NULL
                            """);
                        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN employee_id SET NOT NULL");
                    }
                }
                log.info("Successfully added employee_id column to users table");
            } else {
                log.info("Column employee_id already exists in users table");
            }
            ensureColumnExists("users", "name", "VARCHAR(255)");
            ensureColumnExists("users", "email", "VARCHAR(255)");
            ensureColumnExists("users", "password", "VARCHAR(255)");
            ensureColumnExists("users", "role", "VARCHAR(50)");
            ensureColumnExists("users", "status", "VARCHAR(50)");
            ensureColumnExists("users", "address", "VARCHAR(500)");
            ensureColumnExists("users", "date_of_birth", "DATE");
            ensureColumnExists("users", "blood_group", "VARCHAR(20)");
            ensureColumnExists("users", "valid_document_path", "VARCHAR(255)");
            ensureColumnExists("users", "employee_status", "VARCHAR(50)");
            ensureColumnExists("users", "created_at", "TIMESTAMP");
            ensureColumnExists("users", "updated_at", "TIMESTAMP");
            ensureColumnExists("users", "father_name", "VARCHAR(255)");
            ensureColumnExists("users", "date_of_joining", "DATE");
            ensureColumnExists("users", "office_contact_number", "VARCHAR(20)");
            ensureColumnExists("users", "home_contact_number", "VARCHAR(20)");
            ensureColumnExists("users", "other_contact_number", "VARCHAR(20)");
            ensureColumnExists("users", "identification_mark", "VARCHAR(500)");
            ensureColumnExists("users", "specimen_signature_path", "VARCHAR(255)");
            ensureColumnExists("users", "photo_path", "VARCHAR(255)");
            fixEmployeeStatusConstraint();
            fixBloodGroupColumnLength();
        } catch (Exception e) {
            log.warn("Could not migrate users table: {}", e.getMessage());
        }
    }

    private void renameColumn(String table, String fromCol, String toCol) {
        String from = sanitizeIdent(fromCol);
        String to = sanitizeIdent(toCol);
        String tbl = sanitizeIdent(table);
        if (dbKind == DbKind.MYSQL) {
            jdbcTemplate.execute(String.format("ALTER TABLE `%s` RENAME COLUMN `%s` TO `%s`", tbl, from, to));
        } else {
            jdbcTemplate.execute(String.format("ALTER TABLE %s RENAME COLUMN \"%s\" TO %s", tbl, from, to));
        }
    }

    /** Allow only safe SQL identifiers from metadata-driven renames. */
    private static String sanitizeIdent(String raw) {
        if (raw == null || !raw.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Unsafe SQL identifier: " + raw);
        }
        return raw;
    }

    private void migrateSitesTable() {
        try {
            if (!tableExists("sites")) {
                log.info("Sites table does not exist yet. Hibernate will create it.");
                return;
            }
            if (!columnExists("sites", "is_active")) {
                log.info("Adding missing column: sites.is_active");
                String altSql = dbKind == DbKind.MYSQL
                    ? """
                    SELECT column_name FROM information_schema.columns
                    WHERE table_schema = DATABASE() AND table_name = 'sites'
                    AND (column_name LIKE '%active%' OR column_name LIKE '%Active%')
                    """
                    : """
                    SELECT column_name FROM information_schema.columns
                    WHERE table_name = 'sites'
                    AND (column_name LIKE '%active%' OR column_name LIKE '%Active%')
                    """;
                List<Map<String, Object>> altResults = jdbcTemplate.queryForList(altSql);
                if (!altResults.isEmpty()) {
                    String existingColumn = (String) altResults.get(0).get("column_name");
                    log.info("Found existing column: {}. Renaming to is_active", existingColumn);
                    renameColumn("sites", existingColumn, "is_active");
                } else {
                    if (dbKind == DbKind.MYSQL) {
                        jdbcTemplate.execute("""
                            ALTER TABLE sites
                            ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1
                            """);
                    } else {
                        jdbcTemplate.execute("""
                            ALTER TABLE sites
                            ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true
                            """);
                    }
                }
                log.info("Successfully added is_active column to sites table");
            } else {
                log.info("Column is_active already exists in sites table");
            }
        } catch (Exception e) {
            log.warn("Could not migrate sites table: {}", e.getMessage());
        }
    }

    private static final String[] DEFAULT_CHALLENGE_HEAD_LABELS = {
        "Transport", "Un-Loading", "Crane", "Entry Passes", "Safety Training", "Eqmt Set up",
        "Work Front Delay", "Job Inspection", "Job Clearance", "Power", "Welding", "Coren Manpower",
        "Eqmt Failure", "Tool Damage", "Non-Avlbty - Tools", "Customer Clearance", "Job related Issues",
        "WCR", "Eqmt Despatch", "Work site Closed", "Work Timing Restriction", "Local Manpower Issue"
    };

    private void migrateJobSiteFlexibleRows() {
        try {
            migrateSiteChallengeLinesForUnlimitedRows();
            migrateTechnicianPaymentsAllowDuplicateDays();
        } catch (Exception e) {
            log.warn("Could not migrate job-site flexible row tables: {}", e.getMessage());
        }
    }

    private void migrateSiteChallengeLinesForUnlimitedRows() {
        if (!tableExists("site_challenge_lines")) {
            return;
        }
        boolean hasChallengeIndex = columnExists("site_challenge_lines", "challenge_index");
        boolean hasHeadLabel = columnExists("site_challenge_lines", "head_label");
        boolean hasLineOrder = columnExists("site_challenge_lines", "line_order");
        if (!hasHeadLabel) {
            jdbcTemplate.execute("ALTER TABLE site_challenge_lines ADD COLUMN head_label VARCHAR(512)");
        }
        if (!hasLineOrder) {
            jdbcTemplate.execute(
                "ALTER TABLE site_challenge_lines ADD COLUMN line_order INTEGER NOT NULL DEFAULT 0");
        }
        if (hasChallengeIndex) {
            jdbcTemplate.execute("""
                UPDATE site_challenge_lines SET line_order = challenge_index
                WHERE line_order IS NULL AND challenge_index IS NOT NULL
                """);
            for (int i = 1; i <= DEFAULT_CHALLENGE_HEAD_LABELS.length; i++) {
                String label = DEFAULT_CHALLENGE_HEAD_LABELS[i - 1];
                jdbcTemplate.update("""
                    UPDATE site_challenge_lines SET head_label = ?
                    WHERE challenge_index = ? AND (head_label IS NULL OR TRIM(head_label) = '')
                    """, label, i);
            }
            jdbcTemplate.execute("""
                UPDATE site_challenge_lines SET head_label = CONCAT('Challenge ', CAST(challenge_index AS CHAR(32)))
                WHERE challenge_index IS NOT NULL AND (head_label IS NULL OR TRIM(head_label) = '')
                """);
            jdbcTemplate.execute("""
                UPDATE site_challenge_lines SET head_label = ''
                WHERE head_label IS NULL
                """);
            jdbcTemplate.execute("""
                UPDATE site_challenge_lines SET line_order = 0
                WHERE line_order IS NULL
                """);
        }
        dropAllUniqueConstraintsExceptPk("site_challenge_lines");
        if (hasChallengeIndex) {
            try {
                if (dbKind == DbKind.MYSQL) {
                    jdbcTemplate.execute("ALTER TABLE site_challenge_lines DROP COLUMN challenge_index");
                } else {
                    jdbcTemplate.execute("ALTER TABLE site_challenge_lines DROP COLUMN IF EXISTS challenge_index");
                }
            } catch (Exception e) {
                log.warn("Could not drop site_challenge_lines.challenge_index: {}", e.getMessage());
            }
        }
        log.info("site_challenge_lines migration for unlimited rows attempted");
    }

    private void migrateTechnicianPaymentsAllowDuplicateDays() {
        if (!tableExists("site_technician_daily_payments")) {
            return;
        }
        if (!columnExists("site_technician_daily_payments", "line_order")) {
            jdbcTemplate.execute(
                "ALTER TABLE site_technician_daily_payments ADD COLUMN line_order INTEGER NOT NULL DEFAULT 0");
        }
        dropAllUniqueConstraintsExceptPk("site_technician_daily_payments");
        log.info("site_technician_daily_payments: unique-per-day constraint removed if present");
    }

    private void dropAllUniqueConstraintsExceptPk(String tableName) {
        String tbl = sanitizeIdent(tableName);
        if (dbKind == DbKind.MYSQL) {
            String sql = """
                SELECT DISTINCT INDEX_NAME AS cname FROM information_schema.statistics
                WHERE table_schema = DATABASE() AND LOWER(table_name) = LOWER(?) AND INDEX_NAME <> 'PRIMARY' AND NON_UNIQUE = 0
                """;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, tbl);
            for (Map<String, Object> row : rows) {
                Object nameObj = row.get("cname");
                if (nameObj == null) {
                    continue;
                }
                String cname = sanitizeIdent(nameObj.toString());
                try {
                    jdbcTemplate.execute(String.format("ALTER TABLE `%s` DROP INDEX `%s`", tbl, cname));
                } catch (Exception e) {
                    log.warn("Could not drop unique index {} on {}: {}", cname, tbl, e.getMessage());
                }
            }
        } else {
            String sql = """
                SELECT constraint_name FROM information_schema.table_constraints
                WHERE LOWER(table_name) = LOWER(?) AND constraint_type = 'UNIQUE'
                """;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, tbl);
            for (Map<String, Object> row : rows) {
                Object nameObj = row.get("constraint_name");
                if (nameObj == null) {
                    continue;
                }
                String cname = nameObj.toString();
                try {
                    jdbcTemplate.execute("ALTER TABLE " + tbl + " DROP CONSTRAINT IF EXISTS \"" + cname + "\"");
                } catch (Exception e) {
                    log.warn("Could not drop unique constraint {} on {}: {}", cname, tbl, e.getMessage());
                }
            }
        }
    }

    private void ensureColumnExists(String tableName, String columnName, String columnType) {
        try {
            String tbl = sanitizeIdent(tableName);
            String col = sanitizeIdent(columnName);
            if (columnExists(tableName, columnName)) {
                return;
            }
            log.info("Column {}.{} does not exist. Checking for alternative names...", tableName, columnName);
            String camelCaseName = columnName.substring(0, 1).toLowerCase()
                + (columnName.length() > 1 ? columnName.substring(1) : "");
            String checkAlternativeSql = dbKind == DbKind.MYSQL
                ? """
                SELECT column_name FROM information_schema.columns
                WHERE table_schema = DATABASE() AND table_name = ?
                AND (LOWER(column_name) = LOWER(?) OR column_name = ?)
                """
                : """
                SELECT column_name FROM information_schema.columns
                WHERE table_name = ?
                AND (LOWER(column_name) = LOWER(?) OR column_name = ?)
                """;
            List<Map<String, Object>> altResults = jdbcTemplate.queryForList(
                checkAlternativeSql, tbl, col, camelCaseName);
            if (!altResults.isEmpty()) {
                Object columnNameObj = altResults.get(0).get("column_name");
                if (columnNameObj != null) {
                    String existingColumn = columnNameObj.toString();
                    if (existingColumn != null && !existingColumn.equalsIgnoreCase(columnName)) {
                        log.info("Renaming column {}.{} to {}", tableName, existingColumn, columnName);
                        renameColumn(tbl, existingColumn, col);
                    }
                }
            } else {
                log.info("Adding missing column {}.{}", tableName, columnName);
                if (dbKind == DbKind.MYSQL) {
                    jdbcTemplate.execute(String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s", tbl, col, columnType));
                } else {
                    jdbcTemplate.execute(String.format("ALTER TABLE %s ADD COLUMN %s %s", tbl, col, columnType));
                }
            }
        } catch (Exception e) {
            log.warn("Could not ensure column {}.{} exists: {}", tableName, columnName, e.getMessage());
        }
    }

    private void fixEmployeeStatusConstraint() {
        try {
            if (!columnExists("users", "employee_status")) {
                log.info("employee_status column does not exist, skipping constraint fix");
                return;
            }
            String dropSql = dbKind == DbKind.MYSQL
                ? """
                SELECT constraint_name FROM information_schema.table_constraints
                WHERE table_schema = DATABASE() AND table_name = 'users' AND constraint_type = 'CHECK'
                AND constraint_name LIKE '%employee_status%'
                """
                : """
                SELECT constraint_name FROM information_schema.table_constraints
                WHERE table_name = 'users' AND constraint_type = 'CHECK'
                AND constraint_name LIKE '%employee_status%'
                """;
            List<Map<String, Object>> constraintResults = jdbcTemplate.queryForList(dropSql);
            for (Map<String, Object> constraint : constraintResults) {
                Object constraintNameObj = constraint.get("constraint_name");
                if (constraintNameObj == null) {
                    continue;
                }
                String constraintName = constraintNameObj.toString();
                log.info("Dropping existing check constraint: {}", constraintName);
                try {
                    if (dbKind == DbKind.MYSQL) {
                        String cn = sanitizeIdent(constraintName);
                        jdbcTemplate.execute("ALTER TABLE users DROP CHECK `" + cn + "`");
                    } else {
                        jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS \"" + constraintName + "\"");
                    }
                } catch (Exception e) {
                    log.warn("Could not drop constraint {}: {}", constraintName, e.getMessage());
                }
            }
            log.info("Creating employee_status check constraint");
            jdbcTemplate.execute("""
                ALTER TABLE users
                ADD CONSTRAINT users_employee_status_check
                CHECK (employee_status IN ('ACTIVE', 'ON_LEAVE', 'TERMINATED', 'SUSPENDED') OR employee_status IS NULL)
                """);
            log.info("Successfully fixed employee_status check constraint");
        } catch (Exception e) {
            log.warn("Could not fix employee_status constraint: {}", e.getMessage());
            try {
                if (dbKind == DbKind.MYSQL) {
                    jdbcTemplate.execute("ALTER TABLE users DROP CHECK users_employee_status_check");
                } else {
                    jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_employee_status_check");
                }
            } catch (Exception e2) {
                log.warn("Could not drop default constraint name: {}", e2.getMessage());
            }
        }
    }

    private void fixBloodGroupColumnLength() {
        try {
            if (!columnExists("users", "blood_group")) {
                log.info("blood_group column does not exist yet, Hibernate will create it");
                return;
            }
            String lenSql = dbKind == DbKind.MYSQL
                ? """
                SELECT character_maximum_length FROM information_schema.columns
                WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'blood_group'
                """
                : """
                SELECT character_maximum_length FROM information_schema.columns
                WHERE table_name = 'users' AND column_name = 'blood_group'
                """;
            List<Map<String, Object>> results = jdbcTemplate.queryForList(lenSql);
            if (results.isEmpty()) {
                return;
            }
            Object lengthObj = results.get(0).get("character_maximum_length");
            Integer currentLength = lengthObj instanceof Number ? ((Number) lengthObj).intValue() : 0;
            if (currentLength == null || currentLength < 20) {
                log.info("Updating blood_group column length from {} to 20", currentLength);
                if (dbKind == DbKind.MYSQL) {
                    jdbcTemplate.execute("ALTER TABLE users MODIFY blood_group VARCHAR(20)");
                } else {
                    jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN blood_group TYPE VARCHAR(20)");
                }
            }
        } catch (Exception e) {
            log.warn("Could not fix blood_group column length: {}", e.getMessage());
        }
    }
}
