package com.attendance.system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigration {
    
    private final JdbcTemplate jdbcTemplate;
    
    @EventListener(ApplicationReadyEvent.class)
    @Order(1) // Run BEFORE DataInitializer
    public void onApplicationReady() {
        try {
            log.info("Starting database migration...");
            // Add a small delay to ensure Hibernate has finished schema updates
            Thread.sleep(1000);
            migrateUsersTable();
            migrateSitesTable();
            migrateJobSiteFlexibleRows();
            log.info("✅ Database migration completed successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Migration interrupted");
        } catch (Exception e) {
            log.error("❌ Database migration failed: {}", e.getMessage(), e);
            // Don't fail startup - just log the error and continue
            log.warn("Application will continue despite migration errors");
        }
    }
    
    private void migrateUsersTable() {
        try {
            // First check if table exists
            String checkTableSql = """
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_name = 'users'
            """;
            
            List<Map<String, Object>> tableResults = jdbcTemplate.queryForList(checkTableSql);
            if (tableResults.isEmpty()) {
                log.info("Users table does not exist yet. Hibernate will create it with correct schema.");
                return;
            }
            
            // Check if employee_id column exists
            String checkColumnSql = """
                SELECT column_name 
                FROM information_schema.columns 
                WHERE table_name = 'users' 
                AND column_name = 'employee_id'
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(checkColumnSql);
            
            if (results.isEmpty()) {
                log.info("Adding missing column: users.employee_id");
                
                // Check if there's an existing column with different name (like employeeId)
                String checkAlternativeSql = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'users' 
                    AND (column_name LIKE '%employee%' OR column_name LIKE '%Employee%')
                """;
                
                List<Map<String, Object>> altResults = jdbcTemplate.queryForList(checkAlternativeSql);
                
                if (!altResults.isEmpty()) {
                    String existingColumn = (String) altResults.get(0).get("column_name");
                    log.info("Found existing column: {}. Renaming to employee_id", existingColumn);
                    
                    // Rename existing column
                    jdbcTemplate.execute("ALTER TABLE users RENAME COLUMN \"" + existingColumn + "\" TO employee_id");
                } else {
                    // Add new column
                    jdbcTemplate.execute("""
                        ALTER TABLE users 
                        ADD COLUMN employee_id VARCHAR(50) UNIQUE
                    """);
                    
                    // Update existing rows with generated employee IDs
                    jdbcTemplate.execute("""
                        UPDATE users 
                        SET employee_id = 'EMP' || LPAD(id::text, 6, '0')
                        WHERE employee_id IS NULL
                    """);
                    
                    // Now make it NOT NULL
                    jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN employee_id SET NOT NULL");
                }
                
                log.info("✅ Successfully added employee_id column to users table");
            } else {
                log.info("Column employee_id already exists in users table");
            }
            
            // Check and fix all other column names to ensure they match expected names
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
            
            // Add new required employee profile columns
            ensureColumnExists("users", "father_name", "VARCHAR(255)");
            ensureColumnExists("users", "date_of_joining", "DATE");
            ensureColumnExists("users", "office_contact_number", "VARCHAR(20)");
            ensureColumnExists("users", "home_contact_number", "VARCHAR(20)");
            ensureColumnExists("users", "other_contact_number", "VARCHAR(20)");
            ensureColumnExists("users", "identification_mark", "VARCHAR(500)");
            ensureColumnExists("users", "specimen_signature_path", "VARCHAR(255)");
            ensureColumnExists("users", "photo_path", "VARCHAR(255)");
            
            // Fix employee_status check constraint
            fixEmployeeStatusConstraint();
            
            // Fix blood_group column length
            fixBloodGroupColumnLength();
        } catch (Exception e) {
            log.warn("Could not migrate users table: {}", e.getMessage());
        }
    }
    
    private void migrateSitesTable() {
        try {
            // First check if table exists
            String checkTableSql = """
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_name = 'sites'
            """;
            
            List<Map<String, Object>> tableResults = jdbcTemplate.queryForList(checkTableSql);
            if (tableResults.isEmpty()) {
                log.info("Sites table does not exist yet. Hibernate will create it with correct schema.");
                return;
            }
            
            // Check if is_active column exists
            String checkColumnSql = """
                SELECT column_name 
                FROM information_schema.columns 
                WHERE table_name = 'sites' 
                AND column_name = 'is_active'
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(checkColumnSql);
            
            if (results.isEmpty()) {
                log.info("Adding missing column: sites.is_active");
                
                // Check if there's an existing column with different name
                String checkAlternativeSql = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'sites' 
                    AND (column_name LIKE '%active%' OR column_name LIKE '%Active%')
                """;
                
                List<Map<String, Object>> altResults = jdbcTemplate.queryForList(checkAlternativeSql);
                
                if (!altResults.isEmpty()) {
                    String existingColumn = (String) altResults.get(0).get("column_name");
                    log.info("Found existing column: {}. Renaming to is_active", existingColumn);
                    jdbcTemplate.execute("ALTER TABLE sites RENAME COLUMN \"" + existingColumn + "\" TO is_active");
                } else {
                    // Add new column with default value
                    jdbcTemplate.execute("""
                        ALTER TABLE sites 
                        ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true
                    """);
                }
                
                log.info("✅ Successfully added is_active column to sites table");
            } else {
                log.info("Column is_active already exists in sites table");
            }
        } catch (Exception e) {
            log.warn("Could not migrate sites table: {}", e.getMessage());
        }
    }

    /**
     * Unbounded job-site rows: drop legacy unique constraints, add columns, backfill from old challenge_index.
     */
    private void migrateJobSiteFlexibleRows() {
        try {
            migrateSiteChallengeLinesForUnlimitedRows();
            migrateTechnicianPaymentsAllowDuplicateDays();
        } catch (Exception e) {
            log.warn("Could not migrate job-site flexible row tables: {}", e.getMessage());
        }
    }

    private static final String[] DEFAULT_CHALLENGE_HEAD_LABELS = {
        "Transport", "Un-Loading", "Crane", "Entry Passes", "Safety Training", "Eqmt Set up",
        "Work Front Delay", "Job Inspection", "Job Clearance", "Power", "Welding", "Coren Manpower",
        "Eqmt Failure", "Tool Damage", "Non-Avlbty - Tools", "Customer Clearance", "Job related Issues",
        "WCR", "Eqmt Despatch", "Work site Closed", "Work Timing Restriction", "Local Manpower Issue"
    };

    private void migrateSiteChallengeLinesForUnlimitedRows() {
        String checkTable = """
            SELECT table_name FROM information_schema.tables
            WHERE LOWER(table_name) = LOWER('site_challenge_lines')
            """;
        if (jdbcTemplate.queryForList(checkTable).isEmpty()) {
            return;
        }
        boolean hasChallengeIndex = columnExists("site_challenge_lines", "challenge_index");
        boolean hasHeadLabel = columnExists("site_challenge_lines", "head_label");
        boolean hasLineOrder = columnExists("site_challenge_lines", "line_order");
        if (!hasHeadLabel) {
            jdbcTemplate.execute(
                "ALTER TABLE site_challenge_lines ADD COLUMN IF NOT EXISTS head_label VARCHAR(512)");
        }
        if (!hasLineOrder) {
            jdbcTemplate.execute(
                "ALTER TABLE site_challenge_lines ADD COLUMN IF NOT EXISTS line_order INTEGER");
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
                UPDATE site_challenge_lines SET head_label = CONCAT('Challenge ', CAST(challenge_index AS VARCHAR(32)))
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
                jdbcTemplate.execute("ALTER TABLE site_challenge_lines DROP COLUMN IF EXISTS challenge_index");
            } catch (Exception e) {
                log.warn("Could not drop site_challenge_lines.challenge_index: {}", e.getMessage());
            }
        }
        log.info("✅ site_challenge_lines migration for unlimited rows attempted");
    }

    private void migrateTechnicianPaymentsAllowDuplicateDays() {
        String checkTable = """
            SELECT table_name FROM information_schema.tables
            WHERE LOWER(table_name) = LOWER('site_technician_daily_payments')
            """;
        if (jdbcTemplate.queryForList(checkTable).isEmpty()) {
            return;
        }
        if (!columnExists("site_technician_daily_payments", "line_order")) {
            jdbcTemplate.execute(
                "ALTER TABLE site_technician_daily_payments ADD COLUMN IF NOT EXISTS line_order INTEGER NOT NULL DEFAULT 0");
        }
        dropAllUniqueConstraintsExceptPk("site_technician_daily_payments");
        log.info("✅ site_technician_daily_payments: unique-per-day constraint removed if present");
    }

    private boolean columnExists(String tableName, String columnName) {
        String sql = """
            SELECT column_name FROM information_schema.columns
            WHERE LOWER(table_name) = LOWER(?) AND LOWER(column_name) = LOWER(?)
            """;
        return !jdbcTemplate.queryForList(sql, tableName, columnName).isEmpty();
    }

    private void dropAllUniqueConstraintsExceptPk(String tableName) {
        String sql = """
            SELECT constraint_name FROM information_schema.table_constraints
            WHERE LOWER(table_name) = LOWER(?) AND constraint_type = 'UNIQUE'
            """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, tableName);
        for (Map<String, Object> row : rows) {
            Object nameObj = row.get("constraint_name");
            if (nameObj == null) {
                continue;
            }
            String cname = nameObj.toString();
            try {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP CONSTRAINT IF EXISTS \"" + cname + "\"");
            } catch (Exception e) {
                log.warn("Could not drop unique constraint {} on {}: {}", cname, tableName, e.getMessage());
            }
        }
    }

    private void ensureColumnExists(String tableName, String columnName, String columnType) {
        try {
            String checkSql = """
                SELECT column_name 
                FROM information_schema.columns 
                WHERE table_name = ? 
                AND column_name = ?
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(checkSql, tableName, columnName);
            
            if (results.isEmpty()) {
                log.info("Column {}.{} does not exist. Checking for alternative names...", tableName, columnName);
                
                // Check for camelCase version
                String camelCaseName = columnName.substring(0, 1).toLowerCase() + 
                    (columnName.length() > 1 ? columnName.substring(1) : "");
                
                String checkAlternativeSql = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = ? 
                    AND (LOWER(column_name) = LOWER(?) OR column_name = ?)
                """;
                
                List<Map<String, Object>> altResults = jdbcTemplate.queryForList(
                    checkAlternativeSql, tableName, columnName, camelCaseName);
                
                if (!altResults.isEmpty()) {
                    Object columnNameObj = altResults.get(0).get("column_name");
                    if (columnNameObj != null) {
                        String existingColumn = columnNameObj.toString();
                        if (existingColumn != null && !existingColumn.equals(columnName)) {
                            log.info("Renaming column {}.{} to {}", tableName, existingColumn, columnName);
                            String renameSql = String.format("ALTER TABLE %s RENAME COLUMN \"%s\" TO %s", 
                                tableName, existingColumn, columnName);
                            jdbcTemplate.execute(renameSql);
                        }
                    }
                } else {
                    log.info("Adding missing column {}.{}", tableName, columnName);
                    String addSql = String.format("ALTER TABLE %s ADD COLUMN %s %s", 
                        tableName, columnName, columnType);
                    jdbcTemplate.execute(addSql);
                }
            }
        } catch (Exception e) {
            log.warn("Could not ensure column {}.{} exists: {}", tableName, columnName, e.getMessage());
        }
    }
    
    private void fixEmployeeStatusConstraint() {
        try {
            // Check if employee_status column exists
            String checkColumnSql = """
                SELECT column_name 
                FROM information_schema.columns 
                WHERE table_name = 'users' 
                AND column_name = 'employee_status'
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(checkColumnSql);
            if (results.isEmpty()) {
                log.info("employee_status column does not exist, skipping constraint fix");
                return;
            }
            
            // Drop existing check constraint if it exists
            String dropConstraintSql = """
                SELECT constraint_name 
                FROM information_schema.table_constraints 
                WHERE table_name = 'users' 
                AND constraint_type = 'CHECK' 
                AND constraint_name LIKE '%employee_status%'
            """;
            
            List<Map<String, Object>> constraintResults = jdbcTemplate.queryForList(dropConstraintSql);
            for (Map<String, Object> constraint : constraintResults) {
                Object constraintNameObj = constraint.get("constraint_name");
                if (constraintNameObj != null) {
                    String constraintName = constraintNameObj.toString();
                    log.info("Dropping existing check constraint: {}", constraintName);
                    try {
                        jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS \"" + constraintName + "\"");
                    } catch (Exception e) {
                        log.warn("Could not drop constraint {}: {}", constraintName, e.getMessage());
                    }
                }
            }
            
            // Create new check constraint with correct enum values
            log.info("Creating employee_status check constraint with correct values");
            jdbcTemplate.execute("""
                ALTER TABLE users 
                ADD CONSTRAINT users_employee_status_check 
                CHECK (employee_status IN ('ACTIVE', 'ON_LEAVE', 'TERMINATED', 'SUSPENDED') OR employee_status IS NULL)
            """);
            
            log.info("✅ Successfully fixed employee_status check constraint");
        } catch (Exception e) {
            log.warn("Could not fix employee_status constraint: {}", e.getMessage());
            // Try to drop constraint without recreating (let Hibernate handle it)
            try {
                jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_employee_status_check");
                log.info("Dropped employee_status constraint, Hibernate will recreate it");
            } catch (Exception e2) {
                log.warn("Could not drop constraint: {}", e2.getMessage());
            }
        }
    }
    
    private void fixBloodGroupColumnLength() {
        try {
            // Check if blood_group column exists
            String checkColumnSql = """
                SELECT column_name, character_maximum_length 
                FROM information_schema.columns 
                WHERE table_name = 'users' 
                AND column_name = 'blood_group'
            """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(checkColumnSql);
            if (!results.isEmpty()) {
                Object lengthObj = results.get(0).get("character_maximum_length");
                Integer currentLength = lengthObj != null ? (Integer) lengthObj : 0;
                
                if (currentLength == null || currentLength < 20) {
                    log.info("Updating blood_group column length from {} to 20", currentLength);
                    jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN blood_group TYPE VARCHAR(20)");
                    log.info("✅ Successfully updated blood_group column length to 20");
                } else {
                    log.info("blood_group column already has correct length: {}", currentLength);
                }
            } else {
                log.info("blood_group column does not exist yet, Hibernate will create it");
            }
        } catch (Exception e) {
            log.warn("Could not fix blood_group column length: {}", e.getMessage());
        }
    }
}
