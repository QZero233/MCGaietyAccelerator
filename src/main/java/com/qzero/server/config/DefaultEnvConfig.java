package com.qzero.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:env.properties","file:env.properties"})
@ConfigurationProperties(prefix = "env")
public class DefaultEnvConfig {

    private String javaPath;
    private String commandFilePath;
    private String javaParameter="";
    private String serverJarFileName="server.jar";


    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public String getCommandFilePath() {
        return commandFilePath;
    }

    public void setCommandFilePath(String commandFilePath) {
        this.commandFilePath = commandFilePath;
    }

    public String getJavaParameter() {
        return javaParameter;
    }

    public void setJavaParameter(String javaParameter) {
        this.javaParameter = javaParameter;
    }

    public String getServerJarFileName() {
        return serverJarFileName;
    }

    public void setServerJarFileName(String serverJarFileName) {
        this.serverJarFileName = serverJarFileName;
    }

    @Override
    public String toString() {
        return "DefaultEnvConfig{" +
                "javaPath='" + javaPath + '\'' +
                ", commandFilePath='" + commandFilePath + '\'' +
                ", javaParameter='" + javaParameter + '\'' +
                ", serverJarFileName='" + serverJarFileName + '\'' +
                '}';
    }
}
