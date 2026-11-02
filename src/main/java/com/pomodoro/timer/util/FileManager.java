package com.pomodoro.timer.util;

import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class FileManager {

    private final Path file = Paths.get("data", "sessions.csv");

    public synchronized void saveSession(String type, int minutes) {
        try {
            Files.createDirectories(file.getParent());

            // Create the CSV file if it does not exist
            if (!Files.exists(file)) {
                Files.writeString(file,
                    "Date,Session Type,Duration Minutes\n",
                    StandardOpenOption.CREATE);
            }

            // Update the old CSV format if necessary
            List<String> lines = Files.readAllLines(file);

            if (!lines.isEmpty() &&
                lines.get(0).equals("Date,Focus Minutes")) {

                List<String> updated = new ArrayList<>();
                updated.add("Date,Session Type,Duration Minutes");

                for (int i = 1; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",", 2);

                    if (parts.length == 2) {
                        updated.add(parts[0] + ",FOCUS," + parts[1]);
                    }
                }

                Files.write(file, updated);
            }

            String data = LocalDateTime.now()
                    + "," + type
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