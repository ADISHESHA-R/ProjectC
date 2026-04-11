package com.attendance.system.service;

import com.attendance.system.dto.request.CreateUserRequest;
import com.attendance.system.dto.request.UpdateUserRequest;
import com.attendance.system.dto.response.CreateUserResponse;
import com.attendance.system.dto.response.UserResponse;
import com.attendance.system.entity.User;
import com.attendance.system.enums.EmployeeStatus;
import com.attendance.system.enums.Role;
import com.attendance.system.enums.UserStatus;
import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Store plain password before hashing (to return in response)
        String plainPassword = request.getPassword();
        
        // Check if employeeId already exists
        String employeeId = request.getEmployeeId();
        if (employeeId != null && !employeeId.isEmpty()) {
            if (userRepository.existsByEmployeeId(employeeId)) {
                throw new RuntimeException("Employee ID already exists: " + employeeId);
            }
        } else {
            // Generate employee ID if not provided
            employeeId = generateEmployeeId();
            // Ensure uniqueness
            while (userRepository.existsByEmployeeId(employeeId)) {
                employeeId = generateEmployeeId();
            }
        }
        
        User user = new User();
        user.setEmployeeId(employeeId);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(plainPassword)); // Hash the password
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setBloodGroup(request.getBloodGroup());
        user.setEmployeeStatus(request.getEmployeeStatus() != null ? 
                              request.getEmployeeStatus() : EmployeeStatus.ACTIVE);
        
        // Set new required fields
        user.setFatherName(request.getFatherName());
        user.setDateOfJoining(request.getDateOfJoining());
        user.setOfficeContactNumber(request.getOfficeContactNumber());
        user.setHomeContactNumber(request.getHomeContactNumber());
        user.setOtherContactNumber(request.getOtherContactNumber());
        user.setIdentificationMark(request.getIdentificationMark());
        // Note: photoPath and specimenSignaturePath will be set when files are uploaded
        
        user = userRepository.save(user);
        UserResponse userResponse = mapToUserResponse(user);
        
        // Return response with plain password (only returned once during creation)
        return new CreateUserResponse(
            userResponse,
            plainPassword,
            request.getEmail()
        );
    }
    
    private String generateEmployeeId() {
        String prefix = "EMP";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        return prefix + timestamp;
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        
        if (request.getEmployeeId() != null && !request.getEmployeeId().equals(user.getEmployeeId())) {
            if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
                throw new RuntimeException("Employee ID already exists: " + request.getEmployeeId());
            }
            user.setEmployeeId(request.getEmployeeId());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getBloodGroup() != null) {
            user.setBloodGroup(request.getBloodGroup());
        }
        if (request.getEmployeeStatus() != null) {
            user.setEmployeeStatus(request.getEmployeeStatus());
        }
        
        // Update new required fields
        if (request.getFatherName() != null) {
            user.setFatherName(request.getFatherName());
        }
        if (request.getDateOfJoining() != null) {
            user.setDateOfJoining(request.getDateOfJoining());
        }
        if (request.getOfficeContactNumber() != null) {
            user.setOfficeContactNumber(request.getOfficeContactNumber());
        }
        if (request.getHomeContactNumber() != null) {
            user.setHomeContactNumber(request.getHomeContactNumber());
        }
        if (request.getOtherContactNumber() != null) {
            user.setOtherContactNumber(request.getOtherContactNumber());
        }
        if (request.getIdentificationMark() != null) {
            user.setIdentificationMark(request.getIdentificationMark());
        }
        
        // Update photo and signature paths
        if (request.getPhotoPath() != null) {
            user.setPhotoPath(request.getPhotoPath());
        }
        if (request.getSpecimenSignaturePath() != null) {
            user.setSpecimenSignaturePath(request.getSpecimenSignaturePath());
        }
        
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.delete(user);
    }
    
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmployeeId(String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with employeeId: " + employeeId));
        return mapToUserResponse(user);
    }
    
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return searchUsers(null, null, null, pageable);
    }

    public Page<UserResponse> searchUsers(String search, Role role, UserStatus status, Pageable pageable) {
        String q = (search != null && !search.isBlank()) ? search.trim() : null;
        if (q == null) {
            return userRepository.filterUsers(role, status, pageable).map(this::mapToUserResponse);
        }
        String roleName = role == null ? null : role.name();
        String statusName = status == null ? null : status.name();
        return userRepository.searchUsers(q, roleName, statusName, pageable).map(this::mapToUserResponse);
    }
    
    public List<UserResponse> getAllEmployees() {
        return userRepository.findAll().stream()
            .filter(user -> user.getRole() != null && Role.EMPLOYEE.equals(user.getRole()))
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }
    
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmployeeId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getAddress(),
            user.getDateOfBirth(),
            user.getBloodGroup(),
            user.getValidDocumentPath(),
            user.getEmployeeStatus(),
            user.getFatherName(),
            user.getDateOfJoining(),
            user.getOfficeContactNumber(),
            user.getHomeContactNumber(),
            user.getOtherContactNumber(),
            user.getIdentificationMark(),
            user.getSpecimenSignaturePath(),
            user.getPhotoPath(),
            user.getCreatedAt()
        );
    }
    
    // Helper method to get User entity (for AdminController)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
