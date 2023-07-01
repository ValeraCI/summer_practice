package com.ledivax.exceptions;

public class DataBaseWorkException extends RuntimeException {
    public DataBaseWorkException() {
    }

    public DataBaseWorkException(String message) {
        super(message);
    }

    public DataBaseWorkException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataBaseWorkException(Throwable cause) {
        super(cause);
    }


}
