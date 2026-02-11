package com.project.backend.repositories;

import com.project.backend.models.AppUser;
import com.project.backend.repositories.users.FilterUsersRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, FilterUsersRepository {
    boolean existsByEmail(String email);
    Optional<AppUser> findByToken(String token);
    AppUser findByEmail(String email);
    List<AppUser> findByEmailIn(List<String> emails);
}
