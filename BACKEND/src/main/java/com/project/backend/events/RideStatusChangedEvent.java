package com.project.backend.events;

import com.project.backend.models.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideStatusChangedEvent {
    private Ride ride;
}
