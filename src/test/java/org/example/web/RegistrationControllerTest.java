package org.example.web;

import org.example.user.User;
import org.example.user.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

public class RegistrationControllerTest {

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldShowRegistrationForm() {
        String viewName = registrationController.showRegistrationForm(model);

        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("register", viewName);
    }

    @Test
    public void shouldRegisterUser() {
        User user = new User();

        String viewName = registrationController.registerUser(user);

        verify(userService).saveUser(user);
        assertEquals("redirect:/login", viewName);
    }
}
