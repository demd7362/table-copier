package org.table.exception;

public class InvalidSchemaException extends RuntimeException {
    public InvalidSchemaException() {
        super();
    }


    public InvalidSchemaException(String message) {
        super(message);
    }

    public InvalidSchemaException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSchemaException(Throwable cause) {
        super(cause);
    }

    protected InvalidSchemaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
