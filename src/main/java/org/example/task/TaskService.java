package org.example.task;

import org.example.user.User;
import org.example.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;


    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Task saveTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user);
    }

    public void deleteTask(Long id, User user) {
        Optional<Task> taskOptional = taskRepository.findByIdAndUser(id, user);
        if (taskOptional.isPresent()) {
            taskRepository.delete(taskOptional.get());
        } else {
            throw new IllegalArgumentException("Task not found or not authorized");
        }
    }

    public Task updateTask(Long id, Task updatedTask) {
        User currentUser = userService.getCurrentUser();
        Task existingTask = taskRepository.findByIdAndUser(id, currentUser).orElseThrow(() -> new TaskNotFoundException(id));
        existingTask.setDescription(updatedTask.getDescription());

        return taskRepository.save(existingTask);
    }

    public Page<Task> getTasks(String description, int page, int size, String sort) {
        User currentUser = userService.getCurrentUser();

        // Walidacja i konstrukcja sortowania
        List<Sort.Order> orders = new ArrayList<>();
        String[] sortParams = sort.split(",");
        for (int i = 0; i < sortParams.length; i += 2) {
            String field = sortParams[i];
            String direction = i + 1 < sortParams.length ? sortParams[i + 1] : "asc";
            if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
                throw new IllegalArgumentException("Invalid sort direction: " + direction);
            }
            orders.add(new Sort.Order(Sort.Direction.fromString(direction), field));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        if (description != null && !description.isEmpty()) {
            return taskRepository.findByUserAndDescriptionContaining(currentUser, description, pageable);
        } else {
            return taskRepository.findByUser(currentUser, pageable);
        }
    }
}
