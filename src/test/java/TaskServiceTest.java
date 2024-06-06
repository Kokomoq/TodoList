import org.example.task.Task;
import org.example.task.TaskRepository;
import org.example.task.TaskService;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setDescription("Task 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setDescription("Task 2");

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);
        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getDescription());
        assertEquals("Task 2", result.get(1).getDescription());

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testSaveTask() {
        Task task = new Task();
        task.setDescription("New Task");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.saveTask(task);
        assertEquals("New Task", result.getDescription());

        verify(taskRepository, times(1)).save(task);
    }
    @Test
    public void testGetTaskById_TaskExists() {

        Long taskId = 1L;
        Task task1 = new Task();
        task1.setId(taskId);
        task1.setDescription("Task 1");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        Task task = taskService.getTaskById(taskId);

        assertNotNull(task);
        assertEquals(taskId, task.getId());
        assertEquals("Task 1", task.getDescription());

    }
    @Test
    public void testGetTaskById_TaskDoesNotExists() {

        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        Task task = taskService.getTaskById(taskId);

        assertNull(task);


    }
}
