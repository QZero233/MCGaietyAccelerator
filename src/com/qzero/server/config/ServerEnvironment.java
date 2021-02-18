package com.qzero.server.config;

public class ServerEnvironment {

    private String commandFilePath;
    private String javaPath;
    private String javaParameter;

    public ServerEnvironment() {
    }

    public ServerEnvironment(String commandFilePath, String javaPath, String javaParameter) {
        this.commandFilePath = commandFilePath;
        this.javaPath = javaPath;
        this.javaParameter = javaParameter;
    }

    public String getCommandFilePath() {
        return commandFilePath;
    }

    public void setCommandFilePath(String commandFilePath) {
        this.commandFilePath = commandFilePath;
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
}
