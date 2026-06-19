package com.jordi.ci.error;

public class PipelineLoadException extends RuntimeException{
    
    public PipelineLoadException(String message) {
        super("Failed to read input script: " + message);
    }
    public PipelineLoadException(String message, Throwable cause) {
        super("Failed to read input script: " + message, cause);
    }
}
