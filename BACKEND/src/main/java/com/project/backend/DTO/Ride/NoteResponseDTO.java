package com.project.backend.DTO.Ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDTO {
    private Long rideId;
    private String PassengerMail;
    private String noteText;
}