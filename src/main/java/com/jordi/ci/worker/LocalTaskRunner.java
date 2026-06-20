package com.jordi.ci.worker;

import java.io.IOException;

import com.jordi.ci.error.ProcessExcecutionException;
import com.jordi.ci.worker.pipeline.CITask;

public class LocalTaskRunner implements CITaskRunner{

    @Override
    public boolean runTask(CITask task) throws IOException {
        try {
            Process p = new ProcessBuilder(task.command().split(" "))
                .redirectErrorStream(true)
                .start();
            int code = p.waitFor();
            return code == 0;
        } catch (IOException e) {
            throw new ProcessExcecutionException("File handling error: " + e.getMessage());            
        } catch (InterruptedException e) {
            throw new ProcessExcecutionException("Process interrupted: " + e.getMessage());
        }
    }
    
}
