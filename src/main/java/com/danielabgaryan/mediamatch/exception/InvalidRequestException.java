package com.danielabgaryan.mediamatch.exception;

public class InvalidRequestException extends RuntimeException{
    
    public InvalidRequestException(String message) {
        super(message);
    }
}
