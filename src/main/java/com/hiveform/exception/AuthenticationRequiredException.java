package com.hiveform.exception;

public class AuthenticationRequiredException extends RuntimeException {
    public AuthenticationRequiredException(String message) {
        super(message);
    }
    
    public AuthenticationRequiredException() {
        super("Authentication required");
    }
} 