package org.table.exception;

public class CreateTableException extends RuntimeException {
    public CreateTableException() {
        super();
    }

    public CreateTableException(String message) {
        super(message);
    }

    public CreateTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateTableException(Throwable cause) {
        super(cause);
    }

    protected CreateTableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
