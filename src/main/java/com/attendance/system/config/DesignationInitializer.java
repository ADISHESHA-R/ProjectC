package com.attendance.system.config;

import com.attendance.system.entity.Designation;
import com.attendance.system.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesignationInitializer {

    private final DesignationRepository designationRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(3)
    public void seedDesignations() {
        if (designationRepository.count() > 0) {
            return;
        }
        int order = 0;
        designationRepository.save(d("ADMIN", "Administrator", order++));
        designationRepository.save(d("EMPLOYEE", "Employee", order++));
        designationRepository.save(d("SITE_SUPERVISOR", "Site supervisor", order++));
        designationRepository.save(d("SITE_ENGINEER", "Site engineer", order++));
        designationRepository.save(d("MACHINIST", "Machinist", order++));
    }

    private static Designation d(String code, String label, int sort) {
        Designation x = new Designation();
        x.setCode(code);
        x.setLabel(label);
        x.setSortOrder(sort);
        x.setActive(true);
        return x;
    }
}
