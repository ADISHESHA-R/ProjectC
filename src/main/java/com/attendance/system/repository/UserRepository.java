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
     * Text search: native SQL with CAST(... AS TEXT) so PostgreSQL applies LOWER() to text, not bytea.
     * Role/status use String (enum names) so PostgreSQL compares varchar columns to varchar; binding
     * enums in native queries can use ordinals (smallint) and cause type errors.
     */
    @Query(value = "SELECT u.* FROM users u WHERE " +
           "(LOWER(CAST(u.name AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(u.email AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(u.employee_id AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(CAST(:role AS VARCHAR) IS NULL OR u.role = CAST(:role AS VARCHAR)) AND " +
           "(CAST(:status AS VARCHAR) IS NULL OR u.status = CAST(:status AS VARCHAR))",
           countQuery = "SELECT count(*) FROM users u WHERE " +
           "(LOWER(CAST(u.name AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(u.email AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(u.employee_id AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(CAST(:role AS VARCHAR) IS NULL OR u.role = CAST(:role AS VARCHAR)) AND " +
           "(CAST(:status AS VARCHAR) IS NULL OR u.status = CAST(:status AS VARCHAR))",
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
