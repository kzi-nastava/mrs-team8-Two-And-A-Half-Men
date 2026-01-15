package com.project.backend.service;

import com.project.backend.DTO.ActivateRequestDTO;
import com.project.backend.DTO.RegistretionDTO;
import com.project.backend.DTO.UserLoginRequestDTO;
import com.project.backend.DTO.UserTokenDTO;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenUtils tokenUtils;

    public void registerCustomer(RegistretionDTO userData) throws Exception {
        // Validate user data
        isValid(userData);
        // Create new customer
        Customer customer = new Customer();
        customer.setPassword(passwordEncoder.encode(userData.getPassword()));
        customer.setEmail(userData.getUsername());
        customer.setFirstName(userData.getFirstName());
        customer.setLastName(userData.getLastName());
        customer.setAddress(userData.getAddress());
        customer.setPhoneNumber(userData.getPhoneNumber());
        customer.setIsActive(false);
        customer.setIsBlocked(false);
        customer.setTokenExpiration(LocalDateTime.now().plusDays(3));
        String token = java.util.UUID.randomUUID().toString();
        customer.setToken(token);
        appUserRepository.save(customer);


        // Send conformation email
        emailService.sendVerificationEmail(customer.getEmail(), customer.getFirstName(),
                "http://localhost:4200/activation?token=" + token);
    }

    public String activateAccount(ActivateRequestDTO tokenDTO) throws Exception {
        String token = tokenDTO.getToken();
        System.out.println(token);

        AppUser user = appUserRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Token do not exist"));
        System.out.println(user.getEmail());
        if (user.getIsActive()) {
            return "Account is active";
        }
        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token date experied");
        }
        user.setIsActive(true);
        user.setToken(null);
        user.setTokenExpiration(null);
        appUserRepository.save(user);

        return "Account activated";
    }

    private void isValid(RegistretionDTO userData) {
        if (userData.getUsername() == null || userData.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (userData.getPassword() == null || userData.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Invalid password");
        }
        if (appUserRepository.existsByEmail(userData.getUsername())) {
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
        if (userData.getPhoneNumber() == null || userData.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (!userData.getUsername().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userData.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (!userData.getPhoneNumber().matches("^[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }


    }


    public UserTokenDTO login(UserLoginRequestDTO credentials) throws Exception {
        AppUser customer = appUserRepository.findByEmail(credentials.getUsername());
        if (customer == null) {
            throw new IllegalArgumentException("Invalid username or password");
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

            String jwt = tokenUtils.generateToken(user);
            int expiresIn = tokenUtils.getExpiredIn();
            return new UserTokenDTO(jwt, expiresIn);
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw new IllegalArgumentException("Invalid username or password");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Invalid username or password " + e.getMessage());
        }
    }
}
