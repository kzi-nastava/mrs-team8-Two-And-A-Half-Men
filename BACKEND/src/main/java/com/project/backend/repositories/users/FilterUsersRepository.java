package com.project.backend.repositories.users;

import com.project.backend.DTO.users.UserFilterDTO;
import com.project.backend.DTO.users.UserListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterUsersRepository {
    Page<UserListDTO> findAllWithFilters(UserFilterDTO filters, Pageable pageable);
}