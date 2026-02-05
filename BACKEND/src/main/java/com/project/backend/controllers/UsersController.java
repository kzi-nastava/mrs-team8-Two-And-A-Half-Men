package com.project.backend.controllers;

import com.project.backend.DTO.users.UserFilterDTO;
import com.project.backend.DTO.users.UserListDTO;
import com.project.backend.DTO.users.UserPageableDTO;
import com.project.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')") // Only admins can access these endpoints
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserListDTO>> getAllUsers(@ModelAttribute UserFilterDTO filters, @ModelAttribute UserPageableDTO pageableDTO) {
        return ResponseEntity.ok(userService.getAllUsers(filters, pageableDTO.toPageable()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserDetailDTO> getUserById(@PathVariable Long id) {
//        UserDetailDTO user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
}
