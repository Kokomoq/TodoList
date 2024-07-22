package org.example.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.example.user.User;

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
        user.setUsername("testuser");

        task = new Task();
        task.setId(1L);
        task.setDescription("Task Description");
        task.setUser(user);
    }

    @Test
    public void shouldReturnAllUserTasks() {
        when(taskRepository.findByUser(user)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks(user);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(taskRepository, times(1)).findByUser(user);
    }
    @Test
    public void shouldSaveTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.saveTask(task, user);

        assertNotNull(savedTask);
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(user, savedTask.getUser());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void shouldGetTaskById() {
        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.getTaskById(task.getId(), user);

        assertTrue(foundTask.isPresent());
        assertEquals(task.getDescription(), foundTask.get().getDescription());
        verify(taskRepository, times(1)).findByIdAndUser(task.getId(), user);
    }

    @Test
    public void shouldNotGetTaskById() {
        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.empty());

        Optional<Task> foundTask = taskService.getTaskById(task.getId(), user);

        assertFalse(foundTask.isPresent());
        verify(taskRepository, times(1)).findByIdAndUser(task.getId(), user);
    }

    @Test
    public void shouldDeleteTask() {
        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(task.getId(), user);

        verify(taskRepository, times(1)).findByIdAndUser(task.getId(), user);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findByIdAndUser(task.getId(), user)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(task.getId(), user));

        verify(taskRepository, times(1)).findByIdAndUser(task.getId(), user);
        verify(taskRepository, times(0)).delete(any(Task.class));
    }
}
