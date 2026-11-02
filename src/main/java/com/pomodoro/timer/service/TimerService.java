
package com.pomodoro.timer.service;

import com.pomodoro.timer.model.TimerStatus;
import com.pomodoro.timer.exception.InvalidTimerException;
import com.pomodoro.timer.util.FileManager;

import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class TimerService {

    private final FileManager fileManager;

    private String mode = "FOCUS";
    private long remainingSeconds = 1500;
    private boolean running = false;
    private Instant endTime;

    private int completedSessions = 0;
    private int focusSessions = 0;

    private int focusMinutes = 25;
    private int shortBreakMinutes = 5;
    private int longBreakMinutes = 15;

    public TimerService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    private long getDuration() {
        return switch (mode) {
            case "SHORT_BREAK" -> shortBreakMinutes * 60L;
            case "LONG_BREAK" -> longBreakMinutes * 60L;
            default -> focusMinutes * 60L;
        };
    }

    private void updateTimer() {

        while (running && !Instant.now().isBefore(endTime)) {

            if (mode.equals("FOCUS")) {
                completedSessions++;
                focusSessions++;

                fileManager.saveSession(focusMinutes);

                if (focusSessions % 4 == 0) {
                    mode = "LONG_BREAK";
                } else {
                    mode = "SHORT_BREAK";
                }

            } else {
                mode = "FOCUS";
            }

            remainingSeconds = getDuration();
            endTime = endTime.plusSeconds(remainingSeconds);
        }

        if (running) {
            long millis = java.time.Duration.between(
                    Instant.now(), endTime).toMillis();

            remainingSeconds = Math.max(
                    0, (millis + 999) / 1000);
        }
    }

    public synchronized TimerStatus getStatus() {
        updateTimer();

        return new TimerStatus(
                mode,
                remainingSeconds,
                running,
                completedSessions,
                focusSessions,
                shortBreakMinutes,
                longBreakMinutes
        );
    }

    public synchronized TimerStatus start() {
        updateTimer();

        if (!running) {
            if (remainingSeconds <= 0) {
                remainingSeconds = getDuration();
            }

            endTime = Instant.now().plusSeconds(
                    remainingSeconds);

            running = true;
        }

        return getStatus();
    }

    public synchronized TimerStatus pause() {
        updateTimer();
        running = false;
        return getStatus();
    }

    public synchronized TimerStatus reset() {
        running = false;
        remainingSeconds = getDuration();
        return getStatus();
    }

    public synchronized TimerStatus setMode(String newMode) {

        if (!newMode.equals("FOCUS")
                && !newMode.equals("SHORT_BREAK")
                && !newMode.equals("LONG_BREAK")) {

            throw new InvalidTimerException(
                    "Invalid timer mode");
        }

        mode = newMode;
        running = false;
        remainingSeconds = getDuration();

        return getStatus();
    }

    public synchronized TimerStatus setDurations(
            int focus, int shortBreak, int longBreak) {

        if (focus < 1 || focus > 180
                || shortBreak < 1 || shortBreak > 60
                || longBreak < 1 || longBreak > 120) {

            throw new InvalidTimerException(
                    "Enter valid timer durations");
        }

        focusMinutes = focus;
        shortBreakMinutes = shortBreak;
        longBreakMinutes = longBreak;

        running = false;
        remainingSeconds = getDuration();

        return getStatus();
    }
}
