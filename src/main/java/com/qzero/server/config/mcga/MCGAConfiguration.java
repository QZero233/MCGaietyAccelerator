package com.qzero.server.config.mcga;

import java.util.Map;

public class MCGAConfiguration {

    private String containerName;

    private Map<String,String> mcgaConfig;

    public MCGAConfiguration() {
    }

    public MCGAConfiguration(String containerType, Map<String, String> mcgaConfig) {
        this.containerName = containerType;
        this.mcgaConfig = mcgaConfig;
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
                "containerType='" + containerName + '\'' +
                ", mcgaConfig=" + mcgaConfig +
                '}';
    }
}
