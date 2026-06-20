package com.jordi.ci.worker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import com.jordi.ci.error.ProcessExcecutionException;

import com.jordi.ci.worker.pipeline.*;
@Component
public class YamlRunner {


    
    //Using ansi colors for nice terminal outputs
    private static final String PASSED = AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, "PASSED", AnsiColor.DEFAULT);
    private static final String FAILED = AnsiOutput.toString(AnsiColor.BRIGHT_RED, "FAILED", AnsiColor.DEFAULT);

    //TODO: Consider if REPLoop is better. Probably not, as file size should be small. Process first, report constantly
    
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
    public void runYaml(String yaml, Writer output) throws IOException{
        runScript(yamlParser.parseYaml(yaml), output);
    }

    /**
     * Reads a yaml formatted for this CI and runs it
     * @param yaml a File from a YAML file, formatted to run on this CI 
     * @param output a Writer to write the CI results to
     * @throws IOException
     */
    public void runYaml(File yaml, Writer output) throws IOException{
        runScript(yamlParser.parseYaml(yaml), output);
    }

    /**
     * Runs a whole CIScript, reporting the successes and failures to the given writer
     * @param script a script object representing a CI Pipeline
     * @param output a Writer to write the CI results to
     * @throws IOException
     */
    private void runScript(CIScript script, Writer output) throws IOException {
        int passed = 0;
        int totalStages = script.stages().size();
        //TODO: could maybe extract this to its own method
        for(CIStage stage : script.stages()) {
            String outputSuffix = " stage '" + stage.name() + '\'';
            if(runStage(stage, output)) {
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
    private boolean runStage(CIStage stage, Writer output) throws IOException {
        int passedTasks = 0;
        //TODO: could maybe extract this
        for (CITask task : stage.tasks()) {
        String outputSuffix = " task '" + task.name() 
                    + "' in stage " + "'" + stage.name() + "'" ;
            if (runTask(task)){ 
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

    /**
     * Run a single task, which represents a command or test to run. 
     * Reports back success based on the exit code of the underlying process
     * @param task the task to run
     * @return true on sucess of the underlying process, false otherwise 
     * @throws IOException
     */
    //TODO: this method should be extracted into an interface, then need 2 implementations, Local and Docker
    //TODO: do I pass in a new CITaskRunner to the constructor, or into the function?
    //TODO: do I change this into a real class, or stay?
    private boolean runTask(CITask task) throws IOException{
        try {
            Process p = new ProcessBuilder(task.command().split(" "))
                .redirectErrorStream(true)
                .start();
            int code = p.waitFor();
            if(code != 0) {
                return false;
            }else {
                return true;
            }
        } catch (IOException e) {
            throw new ProcessExcecutionException("File handling error: " + e.getMessage());            
        } catch (InterruptedException e) {
            throw new ProcessExcecutionException("Process interrupted: " + e.getMessage());
        }
    }

    //TODO: consider a runTask that accepts a writer for more precise error reporting
    
    


}
