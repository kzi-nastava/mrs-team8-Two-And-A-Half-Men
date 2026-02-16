package com.project.backend.service;

import com.project.backend.DTO.Auth.UserTokenDTO;
import com.project.backend.DTO.Profile.*;
import com.project.backend.events.ProfilePictureUpdatedEvent;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.models.UpdateRequest;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.*;
import com.project.backend.service.impl.ActivityDriverService;
import com.project.backend.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final TokenUtils tokenUtils;
    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final UpdateRequestRepository updateRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final ActivityDriverService activityDriverService;
    private final static String PROFILE_PICTURE_DIR = "profiles";


    public ProfileDTO getProfile(Long userId, UserRole role) {
        ProfileDTO.ProfileDTOBuilder resultBuilder = ProfileDTO.builder();
        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        resultBuilder.personalInfo(new PersonalInfoDTO(user));
        if (role == UserRole.DRIVER) {
            vehicleRepository.findByDriverId(userId).ifPresent(vehicle ->
                    resultBuilder.vehicleInfo(new VehicleInfoDTO(vehicle))
            );
            updateRequestRepository.findByDriverId(userId).ifPresent(request ->
                    resultBuilder.pendingChangeRequest(new ChangeRequestDTO(request))
            );
            resultBuilder.isWorking(
            activityDriverService.isTakingWork((Driver) user));
        }
        return resultBuilder.build();
    }

    @Transactional(rollbackFor = Exception.class)
    public UpdateProfileResponseDTO updateProfile(Long userId, UserRole role, UpdateProfileRequestDTO body) {
        String validateMessage = body.validate();
        if (validateMessage != null) {
            throw new BadRequestException(validateMessage);
        }

        AppUser user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (role == UserRole.DRIVER) {
            return updateDriverProfile((Driver) user, body);
        }
        if (body.getImgSrc() != null && !body.getImgSrc().startsWith("http") && user.getImgSrc() != null) {
            // Publish an event to delete the old profile picture after the transaction is committed
            eventPublisher.publishEvent(new ProfilePictureUpdatedEvent(
                    Paths.get(System.getProperty("user.dir"), "public", user.getImgSrc()).toString()
            ));
        }
        if (body.getFirstName() != null) {
            user.setFirstName(body.getFirstName());
        }
        if (body.getLastName() != null) {
            user.setLastName(body.getLastName());
        }
        if (body.getPhoneNumber() != null) {
            user.setPhoneNumber(body.getPhoneNumber());
        }
        if (body.getAddress() != null) {
            user.setAddress(body.getAddress());
        }
        if (body.getImgSrc() != null) {
            user.setImgSrc(body.getImgSrc());
        }
        if (body.getEmail() != null) {
            user.setEmail(body.getEmail());
        }
        user = userRepository.save(user);

        return createUpdateProfileResponse(user, body.getEmail() != null);
    }


    private UpdateProfileResponseDTO updateDriverProfile(Driver driver, UpdateProfileRequestDTO body) {
        UpdateRequest updateRequest = updateRequestRepository
                .findByDriverId(driver.getId())
                .orElse(UpdateRequest.builder().driver(driver).build());

        // Personal info
        if (body.getFirstName() != null) {
            updateRequest.setFirstName(body.getFirstName());
        }
        if (body.getLastName() != null) {
            updateRequest.setLastName(body.getLastName());
        }
        if (body.getPhoneNumber() != null) {
            updateRequest.setPhoneNumber(body.getPhoneNumber());
        }
        if (body.getAddress() != null) {
            updateRequest.setAddress(body.getAddress());
        }
        if (body.getImgSrc() != null) {
            updateRequest.setImgSrc(body.getImgSrc());
        }
        if (body.getEmail() != null) {
            updateRequest.setEmail(body.getEmail());
        }

        // Vehicle info
        if (body.getModel() != null) {
            updateRequest.setModel(body.getModel());
        }
        if (body.getLicensePlate() != null) {
            updateRequest.setLicensePlate(body.getLicensePlate());
        }
        if (body.getNumberOfSeats() != null) {
            updateRequest.setNumberOfSeats(body.getNumberOfSeats());
        }
        if (body.getVehicleTypeId() != null) {
            var vehicleType = vehicleTypeRepository
                    .findById(body.getVehicleTypeId())
                    .orElseThrow(() -> new BadRequestException("Vehicle type not found"));
            updateRequest.setVehicleType(vehicleType);
        }
        if (body.getAdditionalServiceIds() != null) {
            var additionalServices = additionalServiceRepository.findAllById(body.getAdditionalServiceIds());
            updateRequest.setAdditionalServices(new HashSet<>(additionalServices));
        }

        updateRequestRepository.save(updateRequest);
        return createUpdateProfileResponse(driver, false);
    }

    private UpdateProfileResponseDTO createUpdateProfileResponse(AppUser user, boolean regenerateToken) {
        ProfileDTO profileDTO = getProfile(user.getId(), user.getRole());
        String accessToken = null;
        if (regenerateToken) {
            accessToken = tokenUtils.generateToken(user);
        }
        return new UpdateProfileResponseDTO(accessToken, profileDTO);
    }

    public UserTokenDTO changePassword(Long id, ChangePasswordDTO body) {

        String oldPassword = body.getOldPassword();
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new BadRequestException("Old password cannot be empty");
        }
        String newPassword = body.getNewPassword();
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BadRequestException("New password cannot be empty");
        }
        String confirmNewPassword = body.getConfirmNewPassword();
        if (confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            throw new BadRequestException("Confirmed new password cannot be empty");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException("New password and confirmed new password do not match");
        }

        AppUser user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        String accessToken = tokenUtils.generateToken(user);
        return new UserTokenDTO(accessToken, tokenUtils.getExpiredIn());
    }

    public Map<String, Object> uploadProfilePicture(Long userId, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is missing");
        }
        if (file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new BadRequestException("Invalid file type, only image files are allowed");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("File must have an original filename");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // Setting up the path of the file
        Path filePath = Paths.get(
                System.getProperty("user.dir"),
                "public",
                "files",
                PROFILE_PICTURE_DIR,
                userId + "_" + UUID.randomUUID() + extension
        );

        try {
            // Get the parent directory path
            Path parentDir = filePath.getParent();

            // Create all nonexistent parent directories
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            // Creating the file
            Files.createFile(filePath);

            // Creating an object of FileOutputStream class
            FileOutputStream out = new FileOutputStream(filePath.toString());
            out.write(file.getBytes());

            // Closing the connection
            out.close();
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("File not found");
        }

        return Map.of(
                "ok", true,
                "filePath", Paths.get("files", PROFILE_PICTURE_DIR, filePath.getFileName().toString()).toString()
        );
    }
}