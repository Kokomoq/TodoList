package org.example.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;


public class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldLoginWithError() {
        String viewName = loginController.login("true", null, model);

        verify(model).addAttribute("error", "Invalid email or password.");
        verify(model, never()).addAttribute(eq("message"), anyString());
        assertEquals("login", viewName);
    }

    @Test
    public void shouldLoginWithLogout() {
        String viewName = loginController.login(null, "true", model);

        verify(model).addAttribute("message", "You have been logged out successfully.");
        verify(model, never()).addAttribute(eq("error"), anyString());
        assertEquals("login", viewName);
    }

    @Test
    public void shouldLoginWithErrorAndLogout() {
        String viewName = loginController.login("true", "true", model);

        verify(model).addAttribute("error", "Invalid email or password.");
        verify(model).addAttribute("message", "You have been logged out successfully.");
        assertEquals("login", viewName);
    }

    @Test
    public void shouldLoginWithoutErrorAndLogout() {
        String viewName = loginController.login(null, null, model);

        verify(model, never()).addAttribute(eq("error"), anyString());
        verify(model, never()).addAttribute(eq("message"), anyString());
        assertEquals("login", viewName);
    }
}
