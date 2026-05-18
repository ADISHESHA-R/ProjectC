package com.attendance.system.entity;

import com.attendance.system.enums.RegisterAttendanceCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "site_attendance_register_cells",
    uniqueConstraints = @UniqueConstraint(name = "uk_site_emp_day", columnNames = {"site_id", "employee_id", "calendar_day"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteAttendanceRegisterCell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(name = "calendar_day", nullable = false)
    private LocalDate calendarDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, length = 5)
    private RegisterAttendanceCode code;
}
