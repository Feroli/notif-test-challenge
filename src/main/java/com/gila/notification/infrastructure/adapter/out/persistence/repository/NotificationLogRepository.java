package com.gila.notification.infrastructure.adapter.out.persistence.repository;

import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.NotificationStatus;
import com.gila.notification.infrastructure.adapter.out.persistence.entity.NotificationLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for notification log persistence operations.
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {

    Page<NotificationLogEntity> findAllByOrderBySentAtDesc(Pageable pageable);

    List<NotificationLogEntity> findAllByOrderBySentAtDesc();

    List<NotificationLogEntity> findByUserIdOrderBySentAtDesc(Long userId);

    List<NotificationLogEntity> findByMessageIdOrderBySentAtDesc(Long messageId);

    List<NotificationLogEntity> findByStatusOrderBySentAtDesc(NotificationStatus status);

    List<NotificationLogEntity> findByChannelOrderBySentAtDesc(NotificationChannel channel);

    @Query("SELECT COUNT(n) FROM NotificationLogEntity n WHERE n.status = :status")
    long countByStatus(NotificationStatus status);

    @Query("SELECT COUNT(n) FROM NotificationLogEntity n WHERE n.channel = :channel AND n.status = :status")
    long countByChannelAndStatus(NotificationChannel channel, NotificationStatus status);

    List<NotificationLogEntity> findBySentAtBetweenOrderBySentAtDesc(LocalDateTime start, LocalDateTime end);
}