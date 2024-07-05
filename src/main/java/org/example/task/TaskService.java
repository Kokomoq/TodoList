package org.example.task;

import org.example.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks(User user) {
        return taskRepository.findByUser(user);
    }

    public Task saveTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user);
    }

    public void deleteTaskByIdAndUser(Long id, User user) {
        Optional<Task> taskOptional = taskRepository.findByIdAndUser(id, user);
        if (taskOptional.isPresent()) {
            taskRepository.delete(taskOptional.get());
        } else {
            throw new IllegalArgumentException("Task not found or not authorized");
        }
    }
}
