package com.project.backend.service.impl;

import com.project.backend.service.DateTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class DateTimeServiceImpl implements DateTimeService {
    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
