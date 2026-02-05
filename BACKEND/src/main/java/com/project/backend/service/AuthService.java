package com.project.backend.service;

import com.project.backend.DTO.Auth.*;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ServerExeption;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Vehicle;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.AdditionalServiceRepository;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.VehicleRepository;
import com.project.backend.repositories.VehicleTypeRepository;
import com.project.backend.util.TokenUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final AppUserRepository appUserRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final EmailBodyGeneratorService emailBodyGeneratorService;

    @Value("${frontend.url}")
    private String frontendUrl;

    public AuthService(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            TokenUtils tokenUtils,
            VehicleRepository vehicleRepository,
            VehicleTypeRepository vehicleTypeRepository,
            AdditionalServiceRepository additionalServiceRepository,
            EmailBodyGeneratorService emailBodyGeneratorService
    ) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.emailBodyGeneratorService = emailBodyGeneratorService;
    }

    public void registerCustomer(RegistretionDTO userData) throws Exception {
        // Validate user data
        isValid(userData);
        // Create new customer
        Customer customer = new Customer();
        customer.setPassword(passwordEncoder.encode(userData.getPassword()));
        customer.setEmail(userData.getEmail());
        customer.setFirstName(userData.getFirstName());
        customer.setLastName(userData.getLastName());
        customer.setAddress(userData.getAddress());
        customer.setPhoneNumber(userData.getPhone());
        customer.setIsActive(false);
        customer.setIsBlocked(false);
        customer.setTokenExpiration(LocalDateTime.now().plusDays(3));
        String token = java.util.UUID.randomUUID().toString();
        customer.setToken(token);
        appUserRepository.save(customer);


        // Send conformation email
        emailService.sendVerificationEmail(customer.getEmail(), customer.getFirstName(),
                frontendUrl + "/activation?token=" + token);
    }

    public String activateAccount(ActivateRequestDTO tokenDTO) {
        String token = tokenDTO.getToken();
        System.out.println(token);

        AppUser user = appUserRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Token do not exist"));
        System.out.println(user.getEmail());
        if (user.getIsActive()) {
            return "Account is active";
        }
        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token date has expired");
        }
        user.setIsActive(true);
        user.setToken(null);
        user.setTokenExpiration(null);
        appUserRepository.save(user);

        return "Account activated";
    }

    private void isValid(RegistretionDTO userData) {
        if (userData.getEmail() == null || userData.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (userData.getPassword() == null || userData.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Invalid password");
        }
        if (appUserRepository.existsByEmail(userData.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userData.getFirstName() == null || userData.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Invalid first name");
        }
        if (userData.getLastName() == null || userData.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Invalid last name");
        }
        if (userData.getAddress() == null || userData.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Invalid address");
        }
        if (userData.getPhone() == null || userData.getPhone().isEmpty()) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (!userData.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userData.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (!userData.getPhone().matches("^[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }


    }

    public UserTokenDTO login(UserLoginRequestDTO credentials) {
        AppUser customer = appUserRepository.findByEmail(credentials.getUsername());
        if (customer == null) {
            throw new BadRequestException("Invalid username or password");
        }
        System.out.println("User found: " + customer.getUsername());
        System.out.println("Active: " + customer.isAccountNonExpired());
        System.out.println("Blocked: " + customer.isAccountNonLocked());
        System.out.println("Enabeld: " + customer.isEnabled());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword()
                    )
            );
            System.out.println("Auth passed");
            System.out.println("Authentication successful for user: " + credentials.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AppUser user = (AppUser) authentication.getPrincipal();

            assert user != null;
            String jwt = tokenUtils.generateToken(user);
            int expiresIn = tokenUtils.getExpiredIn();
            return UserTokenDTO.builder()
                    .accessToken(jwt)
                    .expiresIn((long) expiresIn)
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .imgUrl(user.getImgSrc())
                    .role(user.getRole().name())
                    .build();
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    @Transactional
    public Map<String, Object> registerDriver(RegisterDriverDTO body) throws Exception {

        Pair<Driver, Vehicle> driverAndVehicle = getInitializedDriverAndVehicle(body.getPersonalInfo().getEmail());
        Driver driver = driverAndVehicle.getFirst();
        Vehicle vehicle = driverAndVehicle.getSecond();

        this.initializeDriverPersonalInfo(driver, body.getPersonalInfo());
        this.initializeVehicleInfo(vehicle, body.getVehicleInfo(), driver);

        appUserRepository.save(driver);
        vehicleRepository.save(vehicle);
        appUserRepository.flush();
        vehicleRepository.flush();

        emailService.sendEmail(
                driver.getEmail(),
                "Driver Registration Received",
                emailBodyGeneratorService.generateDriverActivationEmailBody(
                        driver.getFirstName(),
                        this.frontendUrl + "/driver-activation?token=" + driver.getToken()
                )
        );

        return Map.of(
                "ok", true,
                "message", "Driver registered successfully",
                "driverId", driver.getId(),
                "vehicleId", vehicle.getId()
        );
    }

    private Pair<Driver, Vehicle> getInitializedDriverAndVehicle(String email) {
        Driver driver = new Driver();
        Vehicle vehicle = new Vehicle();

        // Check if user with the same email already exists
        var existingUser = appUserRepository.findByEmail(email);
        if (existingUser != null) {
            // If the existing user is not a driver, throw an error - email is taken
            if (existingUser.getRole() != UserRole.DRIVER) {
                throw new BadRequestException("Email is already in use");
            }

            // If the existing user is a driver, check their status
            var existingDriver = (Driver) existingUser;
            // If the driver is not in WAITING_ACTIVATION status, throw an error - driver already exists
            if (existingDriver.getDriverStatus() != DriverStatus.WAITING_ACTIVATION) {
                throw new BadRequestException("Driver with this email already exists");
            }

            // If the driver has a valid token expiration, throw an error - registration in process
            if (
                    existingDriver.getTokenExpiration() != null &&
                            existingDriver.getTokenExpiration().isAfter(LocalDateTime.now())
            ) {
                throw new BadRequestException(
                        "Driver registration is already in process. Please check your email for activation link."
                );
            }

            // Reuse the existing driver object for updating
            driver = existingDriver;
            vehicle = vehicleRepository.findByDriverId(driver.getId()).orElse(new Vehicle());
        }

        return Pair.of(driver, vehicle);
    }

    private void initializeDriverPersonalInfo(Driver driver, RegisterDriverPersonalInfoDTO personalInfo) {
        driver.setFirstName(personalInfo.getFirstName());
        driver.setLastName(personalInfo.getLastName());
        driver.setEmail(personalInfo.getEmail());
        driver.setAddress(personalInfo.getAddress());
        driver.setPhoneNumber(personalInfo.getPhoneNumber());
        driver.setIsActive(false);
        driver.setIsBlocked(false);
        driver.setPassword("");
        driver.setToken(UUID.randomUUID().toString());
        driver.setTokenExpiration(LocalDateTime.now().plusDays(1));
        driver.setDriverStatus(DriverStatus.WAITING_ACTIVATION);
    }

    private void initializeVehicleInfo(Vehicle vehicle, RegisterDriverVehicleInfoDTO vehicleInfo, Driver driver) {
        vehicle.setDriver(driver);
        vehicle.setModel(vehicleInfo.getModel());
        vehicle.setLicensePlate(vehicleInfo.getLicensePlate());
        vehicle.setNumberOfSeats(vehicleInfo.getNumberOfSeats());

        vehicle.setVehicleType(
                vehicleTypeRepository
                        .findById(vehicleInfo.getTypeId())
                        .orElseThrow(() -> new BadRequestException("Vehicle type not found"))
                );

        if (vehicle.getAdditionalServices() != null && !vehicle.getAdditionalServices().isEmpty()) {
            vehicle.setAdditionalServices(
                    new HashSet<>(
                            additionalServiceRepository
                                    .findAllById(vehicleInfo.getAdditionalServicesIds().stream().toList())
                    ));
        } else {
            vehicle.setAdditionalServices(new HashSet<>());
        }
    }
    public void forgetPassword(String email) {
        AppUser user = appUserRepository.findByEmail(email);
        if (user == null) {
            throw new BadRequestException("Email do not exist");
        }
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusHours(2));
        appUserRepository.save(user);
        String body =               emailBodyGeneratorService.generatePasswordResetEmailBody(
                user.getFirstName(),
                this.frontendUrl + "/reset-password?token=" + token
        );
        try {
            emailService.sendEmail(user.getEmail(), "Password Reset Request", body);
        } catch (Exception e) {
            throw new ServerExeption("Failed to send password reset email");
        }
    }
    public void resetPassword(ResetPasswordDTO resetPasswordData) {
        AppUser user = appUserRepository.findByToken(resetPasswordData.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

        if(user.getIsActive() == false){
            throw new BadRequestException("User account is not active");
        }
        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordData.getNewPassword()));
        user.setToken(null);
        user.setTokenExpiration(null);
        appUserRepository.save(user);
    }

    public Map<String, String> activeDriverAccount(DriverActivationDTO driverActivationDTO) {

        if (driverActivationDTO.getAccessToken() == null || driverActivationDTO.getAccessToken().isEmpty()) {
            throw new BadRequestException("Access token is required");
        }

        if (driverActivationDTO.getPassword() == null || driverActivationDTO.getPassword().isEmpty()) {
            throw new BadRequestException("Password cannot be empty");
        }

        if (driverActivationDTO.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        var driver = (Driver) appUserRepository.findByToken(driverActivationDTO.getAccessToken())
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (driver == null || driver.getRole() != UserRole.DRIVER) {
            throw new BadRequestException("Invalid token");
        }

        if (driver.getTokenExpiration() == null || driver.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token has expired");
        }

        if (driver.getIsActive()) {
            throw new BadRequestException("Account is already activated");
        }


        driver.setPassword(passwordEncoder.encode(driverActivationDTO.getPassword()));
        driver.setIsActive(true);
        driver.setDriverStatus(DriverStatus.INACTIVE);

        driver.setToken(null);
        driver.setTokenExpiration(null);
        appUserRepository.save(driver);

        return Map.of(
                "message", "Account activated successfully, you can now log in."
        );
    }
}
