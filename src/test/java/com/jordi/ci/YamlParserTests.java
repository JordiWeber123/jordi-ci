package com.jordi.ci;

public class YamlParserTests {
    
    private static final String BASIC_TEST_FILEPATH = "/home/jordi/programming/projects/ci/src/test/java/com/jordi/ci/basicTest.yaml";
    private static final String BASIC_YAML_STRING = """
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
}
