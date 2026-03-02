package com.attendance.system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(2) // Run after DataInitializer
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigration implements CommandLineRunner {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) {
        try {
            migrateUsersTable();
            migrateSitesTable();
            log.info("✅ Database migration completed successfully");
        } catch (Exception e) {
            log.error("❌ Database migration failed: {}", e.getMessage(), e);
            // Don't fail startup - just log the error
        }
    }
    
    private void migrateUsersTable() {
        try {
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
        } catch (Exception e) {
            log.warn("Could not migrate users table: {}", e.getMessage());
        }
    }
    
    private void migrateSitesTable() {
        try {
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
}
