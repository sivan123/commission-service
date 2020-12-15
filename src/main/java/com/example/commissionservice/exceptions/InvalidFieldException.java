package com.example.commissionservice.exceptions;

public class InvalidFieldException extends IllegalArgumentException{
    public InvalidFieldException(String message) {
        super(message);
    }
}
