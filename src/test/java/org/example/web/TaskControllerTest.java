package org.example.web;

import org.example.task.Task;
import org.example.task.TaskService;
import org.example.user.User;
import org.example.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
    }

    @Test
    public void shouldCreateTask() {
        Task task = new Task();
        when(taskService.saveTask(task, user)).thenReturn(task);

        ResponseEntity<Task> response = taskController.createTask(task, (UserDetails) user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService).saveTask(task, user);
    }
}
