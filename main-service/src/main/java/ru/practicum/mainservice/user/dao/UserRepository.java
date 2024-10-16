package ru.practicum.mainservice.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u FROM User u
        WHERE (:ids IS NULL OR u.id IN :ids)
        ORDER BY u.id
        LIMIT :size OFFSET :from
    """)
    List<User> getSortedUsers(List<Long> ids, Long from, Long size);

    @Query("""
        SELECT u FROM User u
        ORDER BY u.id
        LIMIT :size OFFSET :from
    """)
    List<User> getUsersLimit(Long from, Long size);

    boolean existsByEmail(String email);
}
