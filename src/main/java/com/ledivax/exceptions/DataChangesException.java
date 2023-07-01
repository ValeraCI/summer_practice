package com.ledivax.exceptions;

public class DataChangesException extends RuntimeException {
    public DataChangesException() {
    }

    public DataChangesException(String message) {
        super(message);
    }

    public DataChangesException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataChangesException(Throwable cause) {
        super(cause);
    }
}
