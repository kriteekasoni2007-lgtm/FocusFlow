
package com.pomodoro.timer.service;

import com.pomodoro.timer.model.Task;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private long nextId = 1;

    public List<Task> getTasks() {
        return tasks;
    }

    public Task addTask(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty");
        }

        Task task = new Task(nextId++, title.trim());
        tasks.add(task);

        return task;
    }

    public Task completeTask(Long id) {

        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setCompleted(true);
                return task;
            }
        }

        throw new IllegalArgumentException("Task not found");
    }

    public void deleteTask(Long id) {

        boolean removed = tasks.removeIf(
                task -> task.getId() == id);

        if (!removed) {
            throw new IllegalArgumentException("Task not found");
        }
    }
}
