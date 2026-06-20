package com.jordi.ci.worker.pipeline;

import java.util.List;
//Record representing a group of commands in the same steps, with relevant build dependencies done in previous stages
public record CIStage (String name, List<CITask> tasks) {}