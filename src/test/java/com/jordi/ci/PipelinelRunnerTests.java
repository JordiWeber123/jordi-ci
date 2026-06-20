package com.jordi.ci;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jordi.ci.worker.YamlParser;
import com.jordi.ci.worker.pipeline.*;
import com.jordi.ci.worker.LocalTaskRunner;
import com.jordi.ci.worker.PipelineRunner;



//TODO: Testing suite broken because extracted parser
//TODO: Must test parsing separately and use mocked dependency
@ExtendWith(MockitoExtension.class)
public class PipelinelRunnerTests {
    /*@InjectMocks
    private static YamlRunner runner;
    */
    @InjectMocks 
    PipelineRunner yamlRunner;
    
    @Mock
    YamlParser yamlParser;

    @Test
    void basicScriptTest() {
        StringWriter out = new StringWriter();
        String expectedOut = """
[TASK] PASSED task 'print-greeting' in stage 'setup'
[TASK] PASSED task 'show-date' in stage 'setup'
Passed 2/2 tasks in stage 'setup'
100.0% of tasks
[STAGE] PASSED stage 'setup'
[TASK] PASSED task 'current-directory' in stage 'inspect'
[TASK] PASSED task 'current-user' in stage 'inspect'
[TASK] PASSED task 'list-files' in stage 'inspect'
Passed 3/3 tasks in stage 'inspect'
100.0% of tasks
[STAGE] PASSED stage 'inspect'
[TASK] FAILED task 'count-files' in stage 'teardown'
[TASK] PASSED task 'done-message' in stage 'teardown'
Passed 1/2 tasks in stage 'teardown'
50.0% of tasks
[STAGE] FAILED stage 'teardown'
Passed 2/3 stages
66.66666666666667% of stages
""";
        try {
            CIScript script = new CIScript(List.of(
                new CIStage("setup", List.of(new CITask("print-greeting", "echo \"Hello from the setup stage\""), new CITask("show-date", "date"))), 
                new CIStage("inspect", List.of(new CITask("current-directory", "pwd"), new CITask("current-user", "whoami"), new CITask("list-files", "ls -la"))), 
                new CIStage("teardown", List.of(new CITask("count-files", "ls -1 | wc -l"), new CITask("done-message", "echo \"All stages complete\"")))
            ));
            
            yamlRunner.run(script, new LocalTaskRunner(),out);
        } catch(Exception e) {
            //Shouldn't error, so return FAILURE
            assertNull(e);
        }
        //System.out.println(out.toString());
        assertEquals(expectedOut, out.toString());
        
    }

    @Test
    void consoleOutput() {
        
        PrintWriter consoleWrite = new PrintWriter(System.out);  
        try {
            CIScript script = new CIScript(List.of(
                new CIStage("setup", List.of(new CITask("print-greeting", "echo \"Hello from the setup stage\""), new CITask("show-date", "date"))), 
                new CIStage("inspect", List.of(new CITask("current-directory", "pwd"), new CITask("current-user", "whoami"), new CITask("list-files", "ls -la"))), 
                new CIStage("teardown", List.of(new CITask("count-files", "ls -1 | wc -l"), new CITask("done-message", "echo \"All stages complete\"")))
            ));
            yamlRunner.run(script, new LocalTaskRunner(), consoleWrite);
        } catch(Exception e) {
            //Shouldn't error, so return FAILURE
            assertEquals(true, false);
        }
        assertEquals(true, true);
    }
}
