package com.project.backend.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class SchedulingService {

    private final TaskScheduler taskScheduler;

    public SchedulingService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleTask(LocalDateTime exactTime, Task task) {
        Instant instant = exactTime.atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(
                task::execute,
                instant
        );
    }

    public static interface Task {
        void execute();
    }
}
