package com.jordi.ci.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.jordi.ci.YamlRunner;
import com.jordi.ci.error.PipelineLoadException;

//TODO: upgrade CLI util to ApplicationRunner
@Component
@Profile("cli")
public class YamlRunnerCLI implements CommandLineRunner{

    /*
     * Small local class to implement flush-after-write, because default Java doesn't have that behavior 
     */
    private class FlushWriter extends Writer {
        private final Writer out;

        public FlushWriter(Writer out) {
            this.out = out;
        }

        @Override
        public void close() throws IOException {out.close();}

        @Override
        public void flush() throws IOException {out.flush();}

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            out.write(cbuf, off, len);
            for(char c : cbuf) {
                if (c == '\n') {
                    out.flush();
                    break;
                }
            }
        }

    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 1){
            try {
                File yamlFile = new File(args[args.length -1]);
                System.out.println("New stream wiring");
                YamlRunner.runYaml(yamlFile, new FlushWriter(new OutputStreamWriter(System.out)));
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + args[args.length -1]);
            }catch (PipelineLoadException e) {
                System.out.println(e);
            }
        }else {
            System.out.println("Usage: ci <file-path>");
        }
    }
    
}
