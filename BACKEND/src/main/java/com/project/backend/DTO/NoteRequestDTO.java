package com.project.backend.DTO;

public class NoteRequestDTO {
    private String noteText;

    public NoteRequestDTO() {
    }

    public NoteRequestDTO(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteText() {
        return noteText;
    }

    // Setter
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}