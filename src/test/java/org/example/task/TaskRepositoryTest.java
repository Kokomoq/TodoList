package org.example.task;

import org.example.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private User user;
    private Task task1;
    private Task task2;
    @Before
    public void setUp() {
        user = new User();
        user.setUsername("testuser");
        entityManager.persist(user);

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
        List<Task> foundTasks = taskRepository.findByUser(user);

        assertEquals(2, foundTasks.size());
        assertTrue(foundTasks.contains(task1));
        assertTrue(foundTasks.contains(task2));
    }

    @Test
    public void shouldReturnEmptyWhenFindByIdAndUserWithWrongId() {
        Optional<Task> found = taskRepository.findByIdAndUser(999L, user);
        assertFalse(found.isPresent());
    }

    @Test
    public void shouldReturnEmptyListWhenFindUserWithNoTasks() {
        User newUser = new User();
        newUser.setUsername("newuser");
        entityManager.persist(newUser);
        List<Task> found = taskRepository.findByUser(newUser);

        assertTrue(found.isEmpty());
    }
}
