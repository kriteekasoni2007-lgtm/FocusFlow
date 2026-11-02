
package com.pomodoro.timer.model;

public record TimerStatus(
    String mode,
    long remainingSeconds,
    boolean running,
    int completedSessions,
    int focusSessions,
    int shortBreakMinutes,
    int longBreakMinutes
) {}
