package com.jordi.ci;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class YamlRunnerServiceTests {
    /*@InjectMocks
    private static YamlRunnerService runner;
    */
    private static final String BASIC_TEST_FILEPATH = "/home/jordi/programming/projects/ci/src/test/java/com/jordi/ci/basicTest.yaml";
    @Test
    void basicScriptTest() {
        String yaml = """
stages:
  - name: setup
    tasks:
      - name: print-greeting
        command: echo "Hello from the setup stage"
      - name: show-date
        command: date

  - name: inspect
    tasks:
      - name: current-directory
        command: pwd
      - name: current-user
        command: whoami
      - name: list-files
        command: ls -la

  - name: teardown
    tasks:
      - name: count-files
        command: ls -1 | wc -l
      - name: done-message
        command: echo "All stages complete"
    """;
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
            YamlRunner.runYaml(yaml, out);
        } catch(IOException e) {
            //Shouldn't error, so return FAILURE
            assertEquals(true, false);
        }
        //System.out.println(out.toString());
        assertEquals(expectedOut, out.toString());
        
    }

    @Test
    void basicScriptFile() {
        //TODO: Change this to relative, identify pwd
        File yamlFile = new File(BASIC_TEST_FILEPATH);
        StringWriter out =  new StringWriter(); 
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
            YamlRunner.runYaml(yamlFile, out);
        } catch(IOException e) {
            //Shouldn't error, so return FAILURE
            assertEquals(true, false);
        }
        //System.out.println(out.toString());
        assertEquals(expectedOut, out.toString());
    }

    @Test
    void consoleOutputFromFile() {
        File yamlFile = new File(BASIC_TEST_FILEPATH);
        PrintWriter consoleWrite = new PrintWriter(System.out);  
        try {
            YamlRunner.runYaml(yamlFile, consoleWrite);
        } catch(IOException e) {
            //Shouldn't error, so return FAILURE
            assertEquals(true, false);
        }
        assertEquals(true, true);
    }
}
