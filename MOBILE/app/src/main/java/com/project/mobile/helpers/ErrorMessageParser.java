package com.project.mobile.helpers;

import com.project.mobile.DTO.ErrorResponse;

import retrofit2.Response;

public class ErrorMessageParser {
    public static String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                com.google.gson.Gson gson = new com.google.gson.Gson();
                ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);
                if (errorResponse != null) {
                    return errorResponse.getErrorMessage();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "An error occurred";
    }
}
