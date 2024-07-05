package org.example.task;

import org.example.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");

        task = new Task();
        task.setId(1L);
        task.setDescription("Test task");
        task.setUser(user);

    }

    @Test
    public void shouldGetAllTasks() {

        List<Task> tasks = Arrays.asList(task, new Task());
        when(taskRepository.findByUser(user)).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks(user);

        assertEquals(tasks, result);
        verify(taskRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    public void shouldSaveTask() {

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.saveTask(task, user);

        assertEquals(task, createdTask);
        verify(taskRepository, times(1)).save(task);
    }
    @Test
    public void shouldGetTaskById_TaskExists() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(task);

        Task foundTask = taskService.getTaskById(1L, user);

        assertEquals(task, foundTask);
        verify(taskRepository, times(1)).findByIdAndUser(1L, user);
    }
    @Test
    public void shouldGetTaskById_TaskDoesNotExists() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(null);

        Task foundTask = taskService.getTaskById(1L, user);

        assertEquals(null, foundTask);
        verify(taskRepository, times(1)).findByIdAndUser(1L, user);
    }

    @Test
    public void testDeleteTaskById() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(task);

        boolean isDeleted = taskService.deleteTaskById(1L, user);

        assertTrue(isDeleted);
        verify(taskRepository, times(1)).findByIdAndUser(1L, user);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void testDeleteTaskByIdNotFound() {
        when(taskRepository.findByIdAndUser(1L, user)).thenReturn(null);

        boolean isDeleted = taskService.deleteTaskById(1L, user);

        assertTrue(!isDeleted);
        verify(taskRepository, times(1)).findByIdAndUser(1L, user);
        verify(taskRepository, times(0)).delete(any(Task.class));
    }
}
