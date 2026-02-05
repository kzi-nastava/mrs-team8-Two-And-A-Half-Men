package com.project.backend.service;

import com.project.backend.DTO.users.UserFilterDTO;
import com.project.backend.DTO.users.UserListDTO;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.UpdateRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final UpdateRequestRepository updateRequestRepository;
    private final ProfileService profileService;

    public Page<UserListDTO> getAllUsers(UserFilterDTO filters, Pageable pageable) {
        // Everything happens at DB level now! 🎉
        return appUserRepository.findAllWithFilters(filters, pageable);
    }

    public Object getUserById(Long id) {
        var role = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id))
                .getRole();
        return profileService.getProfile(id, role);
    }
}