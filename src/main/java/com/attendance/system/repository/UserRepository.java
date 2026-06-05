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

    /**
     * List/filter only — no LOWER() on name/email/employee_id (avoids PostgreSQL errors when a column
     * is mis-typed as bytea and the OR-branch is still evaluated).
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> filterUsers(
        @Param("role") Role role,
        @Param("status") UserStatus status,
        Pageable pageable);

    /**
     * Text search — portable SQL (PostgreSQL + MySQL). Role/status bound as enum name strings.
     */
    @Query(value = "SELECT u.* FROM users u WHERE " +
           "(LOWER(COALESCE(u.name, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(COALESCE(u.employee_id, '')) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status)",
           countQuery = "SELECT count(*) FROM users u WHERE " +
           "(LOWER(COALESCE(u.name, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(COALESCE(u.employee_id, '')) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status)",
           nativeQuery = true)
    Page<User> searchUsers(
        @Param("search") String search,
        @Param("role") String role,
        @Param("status") String status,
        Pageable pageable);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    long countByRole(Role role);
    long countByStatus(UserStatus status);
}
