package org.example.web;

import org.example.task.Task;
import org.example.task.TaskService;
import org.example.user.User;
import org.example.user.UserRepository;
import org.example.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    UserRepository userRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "dashboard";
    }
}
