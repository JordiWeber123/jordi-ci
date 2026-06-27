# CI Runner

This tool serves a Continuos Integration test runner written in Java using Spring Boot. Perfect for self-hosting your own CI tooling. It listens for pushes in a specified repo, then runs commands from a .yaml file within the repo. This file decribes the structure of the CI pipeline, including the commands to run when the repo is pushed. 

The .yaml file should be structure in the following manner

```yaml
stages: 
    - name: stage-name
      tasks:
        - name: task-name
          command: echo "Hello World!"
        - name: task-2
          command: echo "Hello World2!"
    - name: stage-2
      tasks:
        - name: test
          command: run-test -args
```

Stages are executed sequentially and a failure within a stage stops the next stages from being executed. 
This is because it is expected that following stages have dependencies on prevoious ones, like a "build" stage followed by a "test" stage.

The CI pipeline writes the output to a provided writer

# Web Hook to GitHub

### WIP

# Log and console dump

### WIP

# Install guide

Clone this repo using SSH: ```git clone git@github.com:JordiWeber123/jordi-ci.git``` or HTTPS: ```git clone https://github.com/JordiWeber123/jordi-ci.git```

For compiling and running the CI, you will need Maven and the JDK to compile it, then run with [Work in Progress]

If you prefer running from a .jar file, you may downlad the Fat JAR. This requires only the Java Runtime Environment (JRE) [Work in Progress]