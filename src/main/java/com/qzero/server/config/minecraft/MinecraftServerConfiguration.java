package com.qzero.server.config.minecraft;

import java.util.Map;

public class MinecraftServerConfiguration {

    private String serverName;
    private String serverJarFileName;
    private String javaPath;
    private String javaParameter;
    private String autoConfigCopy;
    private Map<String,String> customizedServerProperties;

    public MinecraftServerConfiguration() {
    }

    public MinecraftServerConfiguration(String serverName, String serverJarFileName, String javaPath, String javaParameter, String autoConfigCopy, Map<String, String> customizedServerProperties) {
        this.serverName = serverName;
        this.serverJarFileName = serverJarFileName;
        this.javaPath = javaPath;
        this.javaParameter = javaParameter;
        this.autoConfigCopy = autoConfigCopy;
        this.customizedServerProperties = customizedServerProperties;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getAutoConfigCopy() {
        return autoConfigCopy;
    }

    public void setAutoConfigCopy(String autoConfigCopy) {
        this.autoConfigCopy = autoConfigCopy;
    }

    public String getServerJarFileName() {
        return serverJarFileName;
    }

    public void setServerJarFileName(String serverJarFileName) {
        this.serverJarFileName = serverJarFileName;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getJavaParameter() {
        return javaParameter;
    }

    public void setJavaParameter(String javaParameter) {
        this.javaParameter = javaParameter;
    }

    public Map<String, String> getCustomizedServerProperties() {
        return customizedServerProperties;
    }

    public void setCustomizedServerProperties(Map<String, String> customizedServerProperties) {
        this.customizedServerProperties = customizedServerProperties;
    }

    @Override
    public String toString() {
        return "MinecraftServerConfiguration{" +
                "serverName='" + serverName + '\'' +
                ", serverJarFileName='" + serverJarFileName + '\'' +
                ", javaPath='" + javaPath + '\'' +
                ", javaParameter='" + javaParameter + '\'' +
                ", autoConfigCopy='" + autoConfigCopy + '\'' +
                ", customizedServerProperties=" + customizedServerProperties +
                '}';
    }
}
