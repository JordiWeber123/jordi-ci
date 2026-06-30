package com.jordi.ci.worker;

import java.io.IOException;
import java.io.StringWriter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jordi.ci.queue.JobInfo;

@RestController
public class WorkerController {
    private final CIWorker ciWorker;
 
    public WorkerController(CIWorker ciWorker) {
        this.ciWorker = ciWorker;
    }

    @GetMapping("/worker")
    public String workerResult() throws IOException, InterruptedException, Exception {
        StringWriter out = new StringWriter();
        try {
            ciWorker.runCI(out, new JobInfo(1L, 
            "https://github.com/JordiWeber123/jordi-ci.git",
            "jordi-ci",
            "a52a199da13feb86b58215eacd425fc0be957651",
            "queued",
            "28609787fa5e"
        ));
        }catch (Exception e){
            System.out.println("An unexpected error ocurred: \n" + e);
        }
        
        return out.toString();
        
    }
}
