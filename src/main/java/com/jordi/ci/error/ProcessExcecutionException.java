package com.jordi.ci.error;

public class ProcessExcecutionException extends RuntimeException{
    public ProcessExcecutionException(String message) {
        super("Failed to execute process: " + message);
    }
}
