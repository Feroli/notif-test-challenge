package com.gila.notification.infrastructure.adapter.out.persistence;

import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.model.NotificationChannel;
import com.gila.notification.domain.model.User;
import com.gila.notification.domain.port.out.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @PostConstruct
    public void initUsers() {
        users.put(1L, User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .subscribedCategories(Set.of(Category.SPORTS, Category.FINANCE))
                .channels(Set.of(NotificationChannel.SMS, NotificationChannel.EMAIL))
                .build());

        users.put(2L, User.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+1234567891")
                .subscribedCategories(Set.of(Category.MOVIES))
                .channels(Set.of(NotificationChannel.EMAIL))
                .build());

        users.put(3L, User.builder()
                .id(3L)
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .phoneNumber("+1234567892")
                .subscribedCategories(Set.of(Category.SPORTS, Category.MOVIES))
                .channels(Set.of(NotificationChannel.SMS, NotificationChannel.PUSH_NOTIFICATION))
                .build());

        users.put(4L, User.builder()
                .id(4L)
                .name("Alice Brown")
                .email("alice.brown@example.com")
                .phoneNumber("+1234567893")
                .subscribedCategories(Set.of(Category.FINANCE))
                .channels(Set.of(NotificationChannel.PUSH_NOTIFICATION))
                .build());

        users.put(5L, User.builder()
                .id(5L)
                .name("Charlie Wilson")
                .email("charlie.wilson@example.com")
                .phoneNumber("+1234567894")
                .subscribedCategories(Set.of(Category.SPORTS, Category.FINANCE, Category.MOVIES))
                .channels(Set.of(NotificationChannel.SMS, NotificationChannel.EMAIL, NotificationChannel.PUSH_NOTIFICATION))
                .build());

        users.put(6L, User.builder()
                .id(6L)
                .name("Diana Martinez")
                .email("diana.martinez@example.com")
                .phoneNumber("+1234567895")
                .subscribedCategories(Set.of(Category.MOVIES, Category.FINANCE))
                .channels(Set.of(NotificationChannel.EMAIL, NotificationChannel.PUSH_NOTIFICATION))
                .build());

        users.put(7L, User.builder()
                .id(7L)
                .name("Edward Davis")
                .email("edward.davis@example.com")
                .phoneNumber("+1234567896")
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.SMS))
                .build());

        users.put(8L, User.builder()
                .id(8L)
                .name("Fiona Garcia")
                .email("fiona.garcia@example.com")
                .phoneNumber("")  // No phone number
                .subscribedCategories(Set.of(Category.FINANCE, Category.MOVIES))
                .channels(Set.of(NotificationChannel.EMAIL))
                .build());

        users.put(9L, User.builder()
                .id(9L)
                .name("George Lee")
                .email("")  // No email
                .phoneNumber("+1234567897")
                .subscribedCategories(Set.of(Category.SPORTS))
                .channels(Set.of(NotificationChannel.SMS, NotificationChannel.PUSH_NOTIFICATION))
                .build());

        users.put(10L, User.builder()
                .id(10L)
                .name("Helen White")
                .email("helen.white@example.com")
                .phoneNumber("+1234567898")
                .subscribedCategories(new HashSet<>())  // No subscriptions
                .channels(Set.of(NotificationChannel.EMAIL))
                .build());

        log.info("Initialized {} mock users", users.size());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findBySubscribedCategory(Category category) {
        return users.values().stream()
                .filter(user -> user.isSubscribedTo(category))
                .collect(Collectors.toList());
    }
}