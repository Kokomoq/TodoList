package org.example.task;

import org.example.user.User;
import org.example.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicjalizacja mocków

        user = new User();
        user.setUsername("testuser");

        task = new Task();
        task.setId(1L);
        task.setDescription("Task 1");
        task.setUser(user);

        System.out.println("Set up completed");
        System.out.println("TaskService: " + taskService);
        System.out.println("TaskRepository: " + taskRepository);
        System.out.println("UserService: " + userService);
    }

    @Test
    public void shouldSaveTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.saveTask(task, user);

        assertNotNull(createdTask);
        assertEquals(task.getDescription(), createdTask.getDescription());
    }

    @Test
    public void shouldUpdateTask() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findByIdAndUser(anyLong(), any(User.class))).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = new Task();
        updatedTask.setDescription("Updated Task Description");
        Task result = taskService.updateTask(task.getId(), updatedTask);

        assertNotNull(result);
        assertEquals("Updated Task Description", result.getDescription());
    }

    @Test
    public void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findByIdAndUser(anyLong(), any(User.class))).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        Task updatedTask = new Task();
        updatedTask.setDescription("Updated Task");

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(task.getId(), updatedTask);
        });
    }

    @Test
    public void shouldDeleteTask() {
        when(taskRepository.findByIdAndUser(anyLong(), any(User.class))).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(any(Task.class));
        when(userService.getCurrentUser()).thenReturn(user);

        assertDoesNotThrow(() -> {
            taskService.deleteTask(task.getId(), user);
        });

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void shouldGetTaskById() {
        when(taskRepository.findByIdAndUser(anyLong(), any(User.class))).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(user);

        Optional<Task> result = taskService.getTaskById(task.getId(), user);

        assertTrue(result.isPresent());
        assertEquals(task.getDescription(), result.get().getDescription());
    }

    @Test
    public void shouldNotGetTaskById() {
        when(taskRepository.findByIdAndUser(anyLong(), any(User.class))).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        Optional<Task> result = taskService.getTaskById(1L, user);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldReturnAllUserTasks() {
        Page<Task> page = new PageImpl<>(Arrays.asList(task));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.getTasks(null, 0, 10, "id,desc");

        assertNotNull(result);
        assertFalse(result.isEmpty()); // Sprawdź, czy wynik nie jest pusty
        assertEquals(1, result.getTotalElements());
        assertEquals(task.getDescription(), result.getContent().get(0).getDescription());
    }

    @Test
    public void shouldReturnSortedTasks() {
        Page<Task> page = new PageImpl<>(Arrays.asList(task));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("description").ascending());

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findByUserAndDescriptionContaining(any(User.class), anyString(), any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.getTasks("Task", 0, 10, "description,asc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(task.getDescription(), result.getContent().get(0).getDescription());
    }
}
