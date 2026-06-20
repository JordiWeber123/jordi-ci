# CI Runner

This tool serves a Continuos Integration test runner written in Java using Spring Boot. Perfect for self-hosting your own CI tooling. It listens for pushes in a specified repo, then runs commands from a .yaml file within the repo. This file decribes the structure of the CI pipeline, including the commands to run when the repo is pushed. 

The .yaml file should be structure in the following manner

`stages: 
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

Stages are executed sequentially and a failure within a stage stops the next stages from being executed. 
This is because it is expected that following stages have dependencies on prevoious ones, like a "build" stage followed by a "test" stage.

The CI pipeline writes the output to a provided writer

# Web Hook to GitHub

### WIP

# Log and console dump

### WIP
