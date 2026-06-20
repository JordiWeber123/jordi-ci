package com.jordi.ci.worker;

import java.io.IOException;
import com.jordi.ci.worker.pipeline.CITask;

public interface CITaskRunner {
    /**
     * Run a single task, which represents a command or test to run. 
     * Reports back success based on the exit code of the underlying process
     * @param task the task to run
     * @return true on sucess of the underlying process, false otherwise 
     * @throws IOException
     */
    //TODO: define well which errors I'm throwing and where
    public boolean runTask(CITask task) throws Exception;
}
