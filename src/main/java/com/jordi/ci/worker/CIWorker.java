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

import com.jordi.ci.queue.JobInfo;
import com.jordi.ci.queue.QueueService;
import com.jordi.ci.worker.pipeline.CIScript;

@Component
public class CIWorker implements Runnable{
    private static final int MAX_MEM_MB = 512;
    private static final int CPU_NUM = 1;
    private static final int MAX_PIDS = 256;
    private static final long POLL_MS = 1000;
    private static final String CONFIG_FILE = "ci.yaml";
    
    private final PipelineRunner runner;
    private final YamlParser parser;
    private final QueueService queueService;
    
    public CIWorker(PipelineRunner runner, YamlParser yamlParser, QueueService queueService) {
        this.runner = runner;
        this.parser = yamlParser;
        this.queueService = queueService;
    }
    //TODO: consider if passing down the writer is the best option here
    public void runCI(Writer out, JobInfo job) throws IOException, InterruptedException, Exception{        
        //TODO: remove hardcoded image
        String image = "alpine";

        String workspacePath = "tmp/job_" + job.containerId() + "/";
        //Create workspace
        File workSpaceDir = new File(workspacePath);
        if(!workSpaceDir.exists()){
            if(!workSpaceDir.mkdirs()){ 
                throw new IOException("Failed to create directory");
            }
        } 
        //clone repo
        Process p = new ProcessBuilder("git", "clone", job.repoLink())
                .directory(workSpaceDir)
                .redirectErrorStream(true)
                .start();
        int code = p.waitFor();
        if (code != 0) {
            throw new IOException("Failed to git clone, exit code: " + code);
        }
        String repoPath = workspacePath + job.repoName() + "/";
        //spin up docker
        String containerName = "build-" + job.containerId();

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
    }

    public void run(){
        while(true) {
            JobInfo job;
            //TODO: cleanup exceptions
            try {
                job = queueService.claimJob();
            } catch(RuntimeException e) {
                try {
                    Thread.sleep(POLL_MS); 
                } catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }
            
            StringWriter out = new StringWriter();
            //TODO: redo exception handling
            try {
                runCI(out, job);
            } catch(IOException e) {
                return;
            } catch(InterruptedException e) {
                return;
            } catch(Exception e) {
                return;
            }
            System.out.println(out.toString());
        }
    }
}
