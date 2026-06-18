package com.jordi.ci;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class YamlRunner {
    
    private record Stage (String name, String command) {}

    //TODO: MEGA refactor this function
    public void runYaml(String yaml){
        Scanner scan = new Scanner(yaml);
        String line = scan.nextLine();
        int lines = 0;
        //Parse yaml
        if(!(line.startsWith("steps") && line.endsWith(":"))) {
            scan.close();
            throw new IllegalArgumentException("Unexpected keyword at line: " + lines + "\n\"" + line + "\"");
        }
        //Extract stages from YAML
        List<Stage> stages = new ArrayList<>();
        while (scan.hasNext() && (line = scan.nextLine().trim()).startsWith("-")) {
            lines++;
            line = line.replace("-", "");
            String [] parts = line.split(":");
            if(!parts[0].equals("name")) {
                scan.close();
                throw new IllegalArgumentException("Keyword 'name' was expected but found '"+ parts[0] +"' instead");
            }
            String name = parts[1];
            
            line = scan.nextLine().trim();
            lines++;
            parts = line.split(":");
            if(!parts[0].equals("run")) {
                scan.close();
                throw new IllegalArgumentException("Keyword 'run' was expected but found '"+ parts[0] +"' instead");
            }
            
            String command = parts[1];
            //If I chose to execute here, it'd be REPl 
            stages.add(new Stage(name, command));
        }

        scan.close();

        //Executing stages
        for(Stage stage : stages) {
            //Run stage
            try {
                Process p = new ProcessBuilder(stage.command().split(" "))
                    .redirectErrorStream(true)
                    .start();
            int code = p.waitFor();
            if(code != 0) System.out.println("Failed pipeline stage: " + stage.name());
            } catch (IOException e) {
                System.out.println("Failed to read file: " + e);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e);
            }
            
        }
    }
    //TODO: currently extracting all stages, then ready to execute
    //TODO: Consider if REPLoop is better

}
