package com.project.backend.repositories;

import com.project.backend.models.AppUser;
import com.project.backend.models.SupportChat;
import com.project.backend.models.enums.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportChatRepository extends JpaRepository<SupportChat, Long> {
    Optional<SupportChat> findByUser(AppUser user);

    List<SupportChat> findByStatus(ChatStatus status);
}
