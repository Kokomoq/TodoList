package org.example.task;

import org.example.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Optional<Task> findByIdAndUser(Long id, User user);
    List<Task> findByUser(User user);

    void deleteByIdAndUser(Long id, User user);

}
