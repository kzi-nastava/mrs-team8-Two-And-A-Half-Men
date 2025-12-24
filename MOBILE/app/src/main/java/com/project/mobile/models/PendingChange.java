package com.project.mobile.models;

import java.util.Date;

public class PendingChange {
    private String field;
    private String oldValue;
    private String newValue;
    private Date timestamp;

    public PendingChange() {}

    public PendingChange(String field, String oldValue, String newValue) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = new Date();
    }

    // Getters
    public String getField() { return field; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public Date getTimestamp() { return timestamp; }

    // Setters
    public void setField(String field) { this.field = field; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}