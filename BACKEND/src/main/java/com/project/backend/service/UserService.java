package com.project.backend.service;

import com.project.backend.DTO.users.BlockUserDTO;
import com.project.backend.DTO.users.UserFilterDTO;
import com.project.backend.DTO.users.UserListDTO;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
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

    public Object blockUser(Long id, BlockUserDTO body) {
        if (body.getReason() == null || body.getReason().isBlank()) {
            throw new BadRequestException("Block reason cannot be empty");
        }
        var user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsBlocked(true);
        user.setBlockReason(body.getReason());
        appUserRepository.save(user);
        return Map.of(
                "message", "User " + user.getFirstName() +" " + user.getLastName() + " has been blocked."
        );
    }

    public Object unblockUser(Long id) {
        var user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsBlocked(false);
        user.setBlockReason(null);
        appUserRepository.save(user);
        return Map.of(
                "message", "User " + user.getFirstName() +" " + user.getLastName() + " has been unblocked."
        );
    }
}