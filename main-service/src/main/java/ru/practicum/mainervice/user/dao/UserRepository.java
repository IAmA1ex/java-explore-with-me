package ru.practicum.mainervice.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainervice.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u FROM User u
        WHERE (:ids IS NULL OR u.id IN :ids)
        ORDER BY u.id
        LIMIT :size OFFSET :from
    """)
    List<User> getSortedUsers(List<String> ids, Integer from, Integer size);
}
