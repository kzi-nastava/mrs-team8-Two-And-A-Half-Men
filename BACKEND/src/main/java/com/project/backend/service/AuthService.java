package com.project.backend.service;

import com.project.backend.DTO.RegistretionDTO;
import com.project.backend.models.Customer;
import com.project.backend.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private EmailService emailService;

    public void registerCustomer(RegistretionDTO userData) throws Exception{
        // Validate user data
        isValid(userData);
        // Create new customer
        Customer customer = new Customer();
        customer.setPassword(userData.getPassword());
        customer.setEmail(userData.getUsername());
        customer.setFirstName(userData.getFirstName());
        customer.setLastName(userData.getLastName());
        customer.setAddress(userData.getAddress());
        customer.setPhoneNumber(userData.getPhoneNumber());
        customer.setActive(false);
        customer.setBLocked(false);
        customer.setTokenExpiration(LocalDateTime.now().plusDays(3));
        String token = java.util.UUID.randomUUID().toString();
        customer.setToken(token);
        appUserRepository.save(customer);


        // Send conformation email
        emailService.sendVerificationEmail(customer.getEmail(), customer.getFirstName(), "http://localhost:8080/activate?token=" + token);
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
        if(userData.getFirstName() == null || userData.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Invalid first name");
        }
        if(userData.getLastName() == null || userData.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Invalid last name");
        }
        if(userData.getAddress() == null || userData.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Invalid address");
        }
        if(userData.getPhoneNumber() == null || userData.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if(!userData.getUsername().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if(userData.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if(!userData.getPhoneNumber().matches("^[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }


    }


}
