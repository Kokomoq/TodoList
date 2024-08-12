package org.example.task;

import org.example.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testuser");
        entityManager.persist(user);
        entityManager.flush();

        task1 = new Task();
        task1.setDescription("Task 1");
        task1.setUser(user);
        entityManager.persist(task1);

        task2 = new Task();
        task2.setDescription("Task 2");
        task2.setUser(user);
        entityManager.persist(task2);

        entityManager.flush();
    }

    @Test
    public void shouldReturnTaskByIdAndUser() {
        Optional<Task> found = taskRepository.findByIdAndUser(task1.getId(), user);

        assertTrue(found.isPresent());
        assertEquals(task1.getDescription(), found.get().getDescription());
    }

    @Test
    public void shouldReturnTasksByUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> foundTasks = taskRepository.findByUser(user, pageable);

        assertEquals(2, foundTasks.getTotalElements());
        assertTrue(foundTasks.getContent().contains(task1));
        assertTrue(foundTasks.getContent().contains(task2));
    }

    @Test
    public void shouldReturnTasksByUserAndDescriptionContaining() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> foundTasks = taskRepository.findByUserAndDescriptionContaining(user, "Task 1", pageable);

        assertEquals(1, foundTasks.getTotalElements());
        assertTrue(foundTasks.getContent().contains(task1));
    }

    @Test
    public void shouldReturnEmptyWhenFindByIdAndUserWithWrongId() {
        Optional<Task> found = taskRepository.findByIdAndUser(999L, user);
        assertFalse(found.isPresent());
    }

    @Test
    public void shouldReturnEmptyPageWhenFindUserWithNoTasks() {
        User newUser = new User();
        newUser.setUsername("newuser");
        entityManager.persist(newUser);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> found = taskRepository.findByUser(newUser, pageable);

        assertTrue(found.isEmpty());
    }
}
