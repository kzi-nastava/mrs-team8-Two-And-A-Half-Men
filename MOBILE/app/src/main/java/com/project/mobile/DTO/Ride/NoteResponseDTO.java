package com.project.mobile.DTO.Ride;

public class NoteResponseDTO {
    private Long rideId;
    private String passengerMail;
    private String noteText;

    public NoteResponseDTO() {

    }

    public NoteResponseDTO(Long rideId, String passengerMail, String noteText) {
        this.rideId = rideId;
        this.passengerMail = passengerMail;
        this.noteText = noteText;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getPassengerMail() {
        return passengerMail;
    }

    public void setPassengerMail(String passengerMail) {
        this.passengerMail = passengerMail;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
