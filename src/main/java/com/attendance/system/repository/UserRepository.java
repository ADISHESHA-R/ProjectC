package com.attendance.system.repository;

import com.attendance.system.entity.User;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> searchUsers(
        @Param("search") String search,
        @Param("role") Role role,
        @Param("status") UserStatus status,
        Pageable pageable);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    long countByRole(Role role);
    long countByStatus(UserStatus status);
}
