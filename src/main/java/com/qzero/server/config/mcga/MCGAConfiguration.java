package com.qzero.server.config.mcga;

import java.util.Map;

public class MCGAConfiguration {

    private String containerName;

    private String enableLogOutput;

    private Map<String,String> mcgaConfig;

    public MCGAConfiguration() {
    }

    public MCGAConfiguration(String containerName, String enableLogOutput, Map<String, String> mcgaConfig) {
        this.containerName = containerName;
        this.enableLogOutput = enableLogOutput;
        this.mcgaConfig = mcgaConfig;
    }

    public String getEnableLogOutput() {
        return enableLogOutput;
    }

    public void setEnableLogOutput(String enableLogOutput) {
        this.enableLogOutput = enableLogOutput;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Map<String, String> getMcgaConfig() {
        return mcgaConfig;
    }

    public void setMcgaConfig(Map<String, String> mcgaConfig) {
        this.mcgaConfig = mcgaConfig;
    }

    @Override
    public String toString() {
        return "MCGAConfiguration{" +
                "containerName='" + containerName + '\'' +
                ", enableLogOutput='" + enableLogOutput + '\'' +
                ", mcgaConfig=" + mcgaConfig +
                '}';
    }
}
