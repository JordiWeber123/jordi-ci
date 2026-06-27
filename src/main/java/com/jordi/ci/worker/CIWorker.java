package com.jordi.ci.worker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.jordi.ci.worker.pipeline.CIScript;

@Component
public class CIWorker {
    private static final int MAX_MEM_MB = 512;
    private static final int CPU_NUM = 1;
    private static final int MAX_PIDS = 256;
    private static final String CONFIG_FILE = "ci.yaml";
    private final PipelineRunner runner;
    private final YamlParser parser;
    
    public CIWorker(PipelineRunner runner, YamlParser yamlParser) {
        this.runner = runner;
        this.parser = yamlParser;
    }
    //TODO: consider if passing down the writer is the best option here
    public void runCI(Writer out) throws IOException, InterruptedException, Exception{
        //TODO:remove hardcoded job info
        JobInfo jobInfo = new JobInfo(1L, 
            "https://github.com/JordiWeber123/jordi-ci.git",
            "jordi-ci",
            "a52a199da13feb86b58215eacd425fc0be957651",
            "queued",
            "28609787fa5e"
        );
        //TODO: remove hardcoded image
        String image = "alpine";

        String workspacePath = "tmp/job_" + jobInfo.containerId() + "/";
        //Create workspace
        File workSpaceDir = new File(workspacePath);
        if(!workSpaceDir.exists()){
            if(!workSpaceDir.mkdirs()){ 
                throw new IOException("Failed to create directory");
            }
        } 
        //clone repo
        Process p = new ProcessBuilder("git", "clone", jobInfo.repoLink())
                .directory(workSpaceDir)
                .redirectErrorStream(true)
                .start();
        int code = p.waitFor();
        if (code != 0) {
            throw new IOException("Failed to git clone, exit code: " + code);
        }
        String repoPath = workspacePath + jobInfo.repoName() + "/";
        //spin up docker
        String containerName = "build-" + jobInfo.containerId();

        DockerRunner taskRunner;
        try {
            taskRunner = new DockerRunner(containerName, image, repoPath, MAX_MEM_MB, CPU_NUM, MAX_PIDS);
        }catch (IOException e) {
            cleanupWorkspace(workspacePath);
            throw e;
        }
        //Parse yaml
        //This file is hardcoded, should always sit at root of repo
        File yaml = new File(repoPath + CONFIG_FILE);
        CIScript pipeline = parser.parseYaml(yaml);
        //task runner should never be null here, we throw if there was an error earlier
        runner.run(pipeline, taskRunner, out);
        
        taskRunner.close();
        System.out.println("===========Output: ");
        System.out.println(out.toString());
        //Clean up
        cleanupWorkspace(workspacePath);

    }
    
    /**
     * Function to clean up the workspace directory
     * Deletes all the created files and directories, along with the workspace itself
     * @param workSpacePath the path to the workspace to delete
     * @throws IOException if deletion of any file fails
     */
    private static void cleanupWorkspace(String workSpacePath) throws IOException {
        Path rootPath = Paths.get(workSpacePath);
        System.out.println("===========Cleanup: ");
        try (Stream<Path> walk = Files.walk(rootPath)) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(System.out::println)
                .forEach(File::delete);
        }
        /*
        try {
            Files.delete(Path.of(workSpacePath));
            System.out.println();
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", workSpacePath);
        } catch (DirectoryNotEmptyException x) {
            System.err.println("Not empty directory");
        } catch (IOException x) {
            System.err.println(x); 
        } */
    }
}
