package com.project.backend.util;

import com.project.backend.models.Admin;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();
            if (principal instanceof AppUser userDetails) {
                return userDetails;
            }
        return null; // Placeholder
    }
    public Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();
            if (principal instanceof Customer userDetails) {
                return userDetails;
            }
        return null; // Placeholder
    }
    public Admin getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();
            if (principal instanceof Admin userDetails) {
                return userDetails;
            }
        return null; // Placeholder
    }
    public Driver getCurrentDriver() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();
            if (principal instanceof Driver userDetails) {
                return userDetails;
            }
        return null; // Placeholder
    }

}
