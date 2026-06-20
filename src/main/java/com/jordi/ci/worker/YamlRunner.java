package com.jordi.ci.worker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import com.jordi.ci.worker.pipeline.*;
import com.jordi.ci.worker.pipeline.CIStage;
import com.jordi.ci.worker.pipeline.CITask;
@Component
public class YamlRunner {


    
    //Using ansi colors for nice terminal outputs
    private static final String PASSED = AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, "PASSED", AnsiColor.DEFAULT);
    private static final String FAILED = AnsiOutput.toString(AnsiColor.BRIGHT_RED, "FAILED", AnsiColor.DEFAULT);
  
    private final YamlParser yamlParser;

    public YamlRunner(YamlParser yamlParser) {
        this.yamlParser = yamlParser;
    }
    
    /**
     * Reads a yaml formatted for this CI and runs it
     * @param yaml a String from a YAML file, formatted to run on this CI 
     * @param output a writer to place the output
     * @throws IOException
     */
    public void runYaml(String yaml, CITaskRunner runner, Writer output) throws IOException, Exception{
        runScript(yamlParser.parseYaml(yaml), runner, output);
    }

    /**
     * Reads a yaml formatted for this CI and runs it
     * @param yaml a File from a YAML file, formatted to run on this CI 
     * @param output a Writer to write the CI results to
     * @throws IOException
     */
    public void runYaml(File yaml, CITaskRunner runner, Writer output) throws IOException, Exception{
        runScript(yamlParser.parseYaml(yaml), runner, output);
    }

    /**
     * Runs a whole CIScript, reporting the successes and failures to the given writer
     * @param script a script object representing a CI Pipeline
     * @param output a Writer to write the CI results to
     * @throws IOException
     */
    //TODO: deal with future CITaskRunner exceptions
    private void runScript(CIScript script, CITaskRunner runner, Writer output) throws IOException, Exception {
        int passed = 0;
        int totalStages = script.stages().size();
        //TODO: could maybe extract this to its own method
        for(CIStage stage : script.stages()) {
            String outputSuffix = " stage '" + stage.name() + '\'';
            if(runStage(stage, runner, output)) {
                passed++;
                output.write("[STAGE] "+ PASSED + outputSuffix + '\n');
            }else {
                output.write("[STAGE] "+ FAILED + outputSuffix + '\n');
                //TODO: think if either returning or throwing here is better than continuing
                //return;
                //throw new TestFailureException ?
            }
        }

        output.write("Passed " + passed + "/" + script.stages().size() + " stages\n");
        output.write((passed * 100.0) / (float) totalStages + "% of stages\n");
    }

    /**
     * Runs a single stage
     * @param stage the stage to run
     * @param output a Writer to write the stage's results to
     * @return true if stage finished succesfully, false otherwise
     */
    //TODO: deal with future CITaskRunner exceptions
    private boolean runStage(CIStage stage, CITaskRunner runner, Writer output) throws IOException, Exception {
        int passedTasks = 0;
        //TODO: could maybe extract this
        for (CITask task : stage.tasks()) {
        String outputSuffix = " task '" + task.name() 
                    + "' in stage " + "'" + stage.name() + "'" ;
            if (runner.runTask(task)){ 
                passedTasks++;
                output.write("[TASK] " + PASSED + outputSuffix + '\n');
            } else {
                output.write("[TASK] " + FAILED + outputSuffix + '\n');
            }
        }
        output.write("Passed " + passedTasks + "/" + stage.tasks().size() + 
            " tasks in stage '" + stage.name() + "'\n");
        output.write((passedTasks * 100.0) / (float) stage.tasks().size() + "% of tasks\n");
        return passedTasks == stage.tasks().size();
    }
}
