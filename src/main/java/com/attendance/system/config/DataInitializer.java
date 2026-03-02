package com.attendance.system.config;

import com.attendance.system.entity.User;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import com.attendance.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Run after DatabaseMigration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (userRepository.findByEmail("admin@attendance.com").isEmpty()) {
            User admin = new User();
            admin.setEmployeeId("ADMIN001");
            admin.setName("Admin User");
            admin.setEmail("admin@attendance.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin@attendance.com / admin123");
        }
        
        // Create test employee if not exists
        if (userRepository.findByEmail("employee@attendance.com").isEmpty()) {
            User employee = new User();
            employee.setEmployeeId("EMP001");
            employee.setName("Test Employee");
            employee.setEmail("employee@attendance.com");
            employee.setPassword(passwordEncoder.encode("employee123"));
            employee.setRole(Role.EMPLOYEE);
            employee.setStatus(UserStatus.ACTIVE);
            userRepository.save(employee);
            System.out.println("✅ Test employee created: employee@attendance.com / employee123");
        }
    }
}
