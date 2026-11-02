
package com.pomodoro.timer.controller;

import com.pomodoro.timer.service.TimerService;
import com.pomodoro.timer.model.TimerStatus;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/timer")
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @GetMapping
    public TimerStatus getTimer() {
        return timerService.getStatus();
    }

    @PostMapping("/start")
    public TimerStatus start() {
        return timerService.start();
    }

    @PostMapping("/pause")
    public TimerStatus pause() {
        return timerService.pause();
    }

    @PostMapping("/reset")
    public TimerStatus reset() {
        return timerService.reset();
    }

    @PostMapping("/mode")
    public TimerStatus changeMode(
            @RequestBody Map<String, String> data) {

        return timerService.setMode(data.get("mode"));
    }

    @PostMapping("/durations")
    public TimerStatus setDurations(
            @RequestBody Map<String, Integer> data) {

        return timerService.setDurations(
                data.get("focusMinutes"),
                data.get("shortBreakMinutes"),
                data.get("longBreakMinutes")
        );
    }
}
