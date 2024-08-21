package org.example.task;

import org.example.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByIdAndUser(Long id, User user);
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndDescriptionContaining(User user, String description, Pageable pageable);
    Page<Task> findByUserAndPriority(User user, Priority priority, Pageable pageable);
    Page<Task> findByUserAndDescriptionContainingAndPriority(User user, String description, Priority priority, Pageable pageable);

}
