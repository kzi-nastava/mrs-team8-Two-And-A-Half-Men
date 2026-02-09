package com.project.mobile.DTO.Ride;

public class NoteRequestDTO {
    private String noteText;

    public NoteRequestDTO() {}

    public NoteRequestDTO(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String content) {
        this.noteText = content;
    }

}
