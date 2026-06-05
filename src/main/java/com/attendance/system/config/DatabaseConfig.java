package com.attendance.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DatabaseConfig {

    @Bean
    @Profile("local")
    public String databaseType() {
        return "H2 (In-Memory) - Local Development";
    }

    @Bean
    @Profile("render")
    public String databaseTypeRender() {
        return "PostgreSQL - Render Deployment (Shared Database)";
    }

    @Bean
    @Profile("mysql")
    public String databaseTypeMysql() {
        return "MySQL";
    }
}
