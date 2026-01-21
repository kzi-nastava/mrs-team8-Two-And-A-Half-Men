package com.project.backend.exceptions;

public class NoActiveRideException extends RuntimeException {
    public NoActiveRideException(String message) {
        super(message);
    }
}
