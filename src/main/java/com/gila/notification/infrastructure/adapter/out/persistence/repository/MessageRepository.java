package com.gila.notification.infrastructure.adapter.out.persistence.repository;

import com.gila.notification.domain.model.Category;
import com.gila.notification.infrastructure.adapter.out.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for message persistence operations.
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByCategoryOrderByCreatedAtDesc(Category category);
    List<MessageEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    List<MessageEntity> findAllByOrderByCreatedAtDesc();
}