package org.example.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.example.task.Task;
import org.example.task.TaskRepository;
import org.example.task.TaskService;
import org.example.user.User;
import org.example.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private WebApplicationContext context;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testuser");

        task = new Task();
        task.setId(1L);
        task.setUser(user);

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldCreateTask() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(taskService.saveTask(any(Task.class), any(User.class))).thenReturn(task);

        String taskJson = "{\"name\":\"Test Task\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(task.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldDeleteTask() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        doNothing().when(taskService).deleteTask(anyLong(), any(User.class));

        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetALlTasks() throws Exception {
        List<Task> tasks = Arrays.asList(task);
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(taskService.getTasks(null, 0, 10, "id,desc")).thenReturn(new PageImpl<>(tasks, pageable, tasks.size()));

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(task.getId()))
                .andExpect(jsonPath("$.content[0].description").value(task.getDescription()));    }
    @Test
    @WithMockUser(username = "testuser")
    void shouldGetTaskById() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(taskService.getTaskById(anyLong(), any(User.class))).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(task.getId()));
    }

    @Test
    void shouldNotCreateTask() throws Exception {
        String taskJson = "{\"name\":\"Test Task\"}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                        .andExpect(status().isForbidden());
    }
    @Test
    void shouldNotDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotGetTaskById() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
    }
    }