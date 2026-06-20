package com.jordi.ci.worker;

import com.jordi.ci.worker.pipeline.CIScript;


import com.jordi.ci.error.PipelineLoadException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;

import org.springframework.stereotype.Component;


@Component
public class YamlParser {
    /**
     * Parse a String formatted as a YAML into a CIScript object, which represents a script to run on the CI 
     * @param yaml a String containing a yaml file's contents describing the script
     * @return a CIScript containing the stages and commands to be ran by this CI
     * @throws PipelineLoadException in case the parsing fails
     */
    public CIScript parseYaml(String yaml) throws PipelineLoadException{
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        try {
            return yamlMapper.readValue(yaml, CIScript.class);
        } catch (Exception e) {
            throw new PipelineLoadException(yaml, e);
        }
    }

    /**
     * Parse a YAML file into a CIScript object, which represents a script to run on the CI 
     * @param yamlFile a File containing the script
     * @return a CIScript containing the stages and commands to be ran by this CI
     * @throws PipelineLoadException in case the parsing fails
     */
    public CIScript parseYaml(File yamlFile) throws PipelineLoadException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        try {
            return yamlMapper.readValue(yamlFile, CIScript.class);
        } catch (Exception e) {
            throw new PipelineLoadException(yamlFile.toString(), e);
        }
    }
}
