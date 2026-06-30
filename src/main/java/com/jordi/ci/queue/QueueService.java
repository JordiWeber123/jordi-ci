package com.jordi.ci.queue;

import org.springframework.stereotype.Service;
import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class QueueService {
    private final QueueRepository queueRepository;
    
    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    /**
     * Gets the first job, sets its status in the Queue to "running" and returns it
     * @return the claimed job, in its old status
     * @throws RuntimeException if the job is not found in the Queue
     */
    @Transactional
    //TODO: Error handling
    public JobInfo claimJob() throws RuntimeException{
        Optional<Job> claimedJobOpt = queueRepository.findFirstByStatus("queued");
        if(claimedJobOpt.isEmpty()) {
            //TODO: better error handling
            throw new RuntimeException("No job found");
        }
        Job claimedJob = claimedJobOpt.get();
        JobInfo foundJob = new JobInfo(claimedJob.getId(),claimedJob.getRepoLink(),
                            claimedJob.getRepoName(),claimedJob.getSHA(),
                            claimedJob.getStatus(),claimedJob.getContainerId()); 
        claimedJob.setStatus("running"); //This will mutate the database
        return foundJob;
    }    

    /**
     * Clears all entries from the queue
     */
    @Transactional
    public void clearQueue() {
        queueRepository.deleteAll();
    }

    /**
     * Saves the given job to the queue
     * @param job
     */
    @Transactional
    public void saveJob(JobInfo job) {
        queueRepository.save(new Job(job.repoLink(), job.repoName(), 
            job.SHA(), job.status(), job.containerId()));
    }
}
