package org.example.task;

import org.example.Main;
import org.example.task.Task;
import org.example.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ContextConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
    }
    @Test
    public void shouldSaveTask() {
        Task task = new Task();
        task.setDescription("Test Task");
        Task savedTask = taskRepository.save(task);

        assertNotNull(savedTask.getId());
        assertEquals("Test Task", task.getDescription());
    }
    @Test
    public void shouldFindById() {
        Task task = new Task();
        task.setDescription("Test Task");
        Task savedTask = taskRepository.save(task);

        Optional<Task> retrievedTask = taskRepository.findById(savedTask.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("Test Task", retrievedTask.get().getDescription());
    }
    @Test
    public void shouldFindAll() {
        Task task1 = new Task();
        task1.setDescription("Test Task");

        Task task2 = new Task();
        task1.setDescription("Test Task");

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(2);
        assertThat(taskList).contains(task1, task2);
}
    @Test
    public void shouldDeleteById() {
        Task task1 = new Task();
        task1.setDescription("Task 1");

        Task task2 = new Task();
        task2.setDescription("Task 2");

        taskRepository.save(task1);
        taskRepository.save(task2);

        Long id1 = task1.getId();
        taskRepository.deleteById(id1);

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(1);
        assertThat(taskList).doesNotContain(task1);
        assertThat(taskList).contains(task2);
    }

}