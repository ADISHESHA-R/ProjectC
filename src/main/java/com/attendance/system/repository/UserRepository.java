package com.attendance.system.repository;

import com.attendance.system.entity.User;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    long countByRole(Role role);
    long countByStatus(UserStatus status);
}
