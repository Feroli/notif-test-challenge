package com.gila.notification.domain.port.out;

import com.gila.notification.domain.model.Category;
import com.gila.notification.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    List<User> findAll();
    List<User> findBySubscribedCategory(Category category);
}