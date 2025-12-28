package com.project.backend.DTO;

public class NoteDTO {
    private String noteText;

    public NoteDTO() {
    }

    public NoteDTO(String noteText) {
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