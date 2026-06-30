package com.jordi.ci.queue;

import jakarta.persistence.*;

@Entity
@Table(name = "jobqueue")
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repoLink;
    private String repoName;
    private String SHA;
    private String status;
    private String containerId;

    public Job(String repoLink, String repoName, String SHA, String status, String containerId) {
        this.repoLink = repoLink;
        this.repoName = repoName;
        this.SHA = SHA;
        this.status = status;
        this.containerId = containerId; 
    }

    public String getContainerId() {
        return containerId;
    }

    public Long getId() {
        return id;
    }

    public String getRepoLink() {
        return repoLink;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getSHA() {
        return SHA;
    }

    public String getStatus() {
        return status;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public void setRepoLink(String repoLink) {
        this.repoLink = repoLink;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setSHA(String sHA) {
        SHA = sHA;
    }

    public void setStatus(String status) {
        if(!status.equals("queued") || !status.equals("running") 
            || !status.equals("passed") || !status.equals("failed")) {
            
            throw new IllegalArgumentException("status must be one of: 'queued', 'running', 'passed', 'failed'");
        }
        
        this.status = status;
    }

    
}
