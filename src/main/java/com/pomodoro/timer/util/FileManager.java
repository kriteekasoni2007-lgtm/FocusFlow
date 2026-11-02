
package com.pomodoro.timer.util;

import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class FileManager {

    private final Path file = Paths.get("data", "sessions.csv");

    public synchronized void saveSession(int minutes) {
        try {
            Files.createDirectories(file.getParent());

            if (!Files.exists(file)) {
                Files.writeString(file,
                    "Date,Focus Minutes\n",
                    StandardOpenOption.CREATE);
            }

            String data = LocalDateTime.now()
                    + "," + minutes + "\n";

            Files.writeString(file, data,
                    StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    public List<String> readSessions() {
        try {
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }

            return Files.readAllLines(file)
                    .stream()
                    .skip(1)
                    .toList();

        } catch (IOException e) {
            System.out.println("Read Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
