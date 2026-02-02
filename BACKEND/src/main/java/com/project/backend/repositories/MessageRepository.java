package com.project.backend.repositories;

import com.project.backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.timestamp DESC LIMIT 1")
    Message findLastMessageByChatId(Long chatId);

    List<Message> findByChatIdOrderByTimestamp(Long chatId);

    @Modifying
    @Query("UPDATE Message m SET m.adminRead = true WHERE m.chat.id = :chatId")
    void markAdminRead(Long chatId);

    @Modifying
    @Query("UPDATE Message m SET m.userRead = true WHERE m.chat.id = :chatId")
    void markUserRead(Long chatId);
}
