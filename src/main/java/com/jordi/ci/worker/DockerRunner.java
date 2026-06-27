package com.jordi.ci.worker;

import java.io.IOException;
import java.io.InputStream;

import com.jordi.ci.worker.pipeline.CITask;

public class DockerRunner implements AutoCloseable, CITaskRunner{
    private final String containerName;
    public DockerRunner(String containerName, String image, String mountPath, int maxMem, int cpus, int pidsLimit) throws IOException, InterruptedException{
        this.containerName = containerName;
        Process p = new ProcessBuilder("docker", "run", 
                                        "-d", "--name", containerName, 
                                        "-v",  System.getProperty("user.dir")+ mountPath + ":/workspace", "-w", "/workspace",
                                        "--memory=" + maxMem + "m", "--cpus=" + cpus, "--pids-limit=" + pidsLimit,
                                        image, "tail", "-f", "/dev/null")
                .redirectErrorStream(true)
                .start();
        
        int code = p.waitFor();
        if (code != 0) {
            //TODO: redo which errors I throw
            close();
            throw new IOException("Docker could not be created, exit code:" + code + "\n");
        }
    }


    @Override
    public void close() throws IOException, InterruptedException {
        Process p = new ProcessBuilder("docker", "rm", 
                                        "-f", containerName)
                .redirectErrorStream(true)
                .start();
        int code = p.waitFor();
        if (code != 0) {
            //TODO: redo which errors I throw
            throw new IOException("Failed to remove docker on close");
        }
    }

    public boolean runTask(CITask task) throws IOException, InterruptedException{
        Process p = new ProcessBuilder("docker", "exec", containerName, 
                                        "sh", "-c", task.command())
                .redirectErrorStream(true)
                .start();
        int code = p.waitFor();
        return code == 0;
    }


}
