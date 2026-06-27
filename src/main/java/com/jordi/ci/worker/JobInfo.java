package com.jordi.ci.worker;

public record JobInfo(
    Long id, 
    String repoLink,
    String repoName,
    String SHA,
    String status,
    String containerId
) {}
