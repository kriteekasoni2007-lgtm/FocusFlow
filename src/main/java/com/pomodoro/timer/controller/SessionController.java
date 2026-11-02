
package com.pomodoro.timer.controller;

import com.pomodoro.timer.util.FileManager;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final FileManager fileManager;

    public SessionController(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @GetMapping
    public List<String> getSessions() {
        return fileManager.readSessions();
    }
}
