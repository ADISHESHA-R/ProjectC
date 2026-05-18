package com.attendance.system.service;

import com.attendance.system.dto.response.DesignationResponse;
import com.attendance.system.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignationService {

    private final DesignationRepository designationRepository;

    public List<DesignationResponse> listActive() {
        return designationRepository.findByActiveTrueOrderBySortOrderAscIdAsc().stream()
            .map(d -> new DesignationResponse(d.getId(), d.getCode(), d.getLabel(), d.getSortOrder()))
            .collect(Collectors.toList());
    }
}
