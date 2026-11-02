
package com.pomodoro.timer.controller;

import com.pomodoro.timer.model.Task;
import com.pomodoro.timer.service.TaskService;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.getTasks();
    }

    @PostMapping
    public Task addTask(
            @RequestBody Map<String, String> data) {

        return taskService.addTask(data.get("title"));
    }

    @PatchMapping("/{id}/complete")
    public Task completeTask(@PathVariable Long id) {
        return taskService.completeTask(id);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return Map.of("message", "Task deleted successfully");
    }
}
