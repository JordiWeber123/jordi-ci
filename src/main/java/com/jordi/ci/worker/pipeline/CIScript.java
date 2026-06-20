package com.jordi.ci.worker.pipeline;

import java.util.List;

//Record representing a group of stages, with their own tasks each 
public record CIScript (List<CIStage> stages) {}