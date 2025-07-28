package com.hiveform.handler;

public class AuthenticationRequiredException extends RuntimeException {
    public AuthenticationRequiredException(String message) {
        super(message);
    }
    
    public AuthenticationRequiredException() {
        super("Authentication required");
    }
} 