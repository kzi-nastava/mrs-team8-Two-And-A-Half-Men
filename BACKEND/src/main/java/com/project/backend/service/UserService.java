package com.project.backend.service;

import com.project.backend.DTO.users.UserFilterDTO;
import com.project.backend.DTO.users.UserListDTO;
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

    public Page<UserListDTO> getAllUsers(UserFilterDTO filters, Pageable pageable) {
        // Everything happens at DB level now! 🎉
        return appUserRepository.findAllWithFilters(filters, pageable);
    }
//    public UserDetailDTO getUserById(Long id) {
//        AppUser user = appUserRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
//
//        UpdateRequest updateRequest = null;
//        if (user instanceof Driver) {
//            updateRequest = updateRequestRepository
//                    .findByDriver((Driver) user)
//                    .orElse(null);
//        }
//
//        return mapToUserDetailDTO(user, updateRequest);
//    }

//    private UserDetailDTO mapToUserDetailDTO(AppUser user, UpdateRequest updateRequest) {
//        UserDetailDTO.UserDetailDTOBuilder builder = UserDetailDTO.builder()
//                .id(user.getId())
//                .firstName(user.getFirstName())
//                .lastName(user.getLastName())
//                .email(user.getEmail())
//                .address(user.getAddress())
//                .phoneNumber(user.getPhoneNumber())
//                .imgSrc(user.getImgSrc())
//                .role(user.getRole())
//                .isActive(user.getIsActive())
//                .isBlocked(user.getIsBlocked())
//                .pendingApproval(false) // Implement your logic
//                .hasPanic(false); // Implement your logic
//
//        if (user instanceof Driver) {
//            Driver driver = (Driver) user;
//            builder.driverStatus(driver.getDriverStatus());
//            builder.hasPendingRequests(updateRequest != null);
//        } else {
//            builder.hasPendingRequests(false);
//        }
//
//        return builder.build();
//    }
}