package com.zacurrya.httpserver.core.io;

public class ReadFileException extends Exception {
    public ReadFileException(Throwable cause) {
        super(cause);
    }

    public ReadFileException(String message) {
        super(message);
    }

    public ReadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
