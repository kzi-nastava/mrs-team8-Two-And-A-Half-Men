package com.project.mobile.DTO;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;


    public String getErrorMessage() {
        if (error != null && !error.isEmpty()) return error;
        if (message != null && !message.isEmpty()) return message;
        return "An error occurred";
    }
}