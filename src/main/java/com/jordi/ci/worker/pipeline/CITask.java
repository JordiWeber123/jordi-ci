package com.jordi.ci.worker.pipeline;

/**
 * This class represents a single task to execute. It is given a name for logging
 */
public record CITask (String name, String command) {}
