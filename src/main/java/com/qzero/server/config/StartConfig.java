package com.qzero.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:start.properties","file:start.properties"})
@ConfigurationProperties(prefix = "start")
public class StartConfig {

    private boolean localConsoleMode=false;
    private String localConsolePath;

    private boolean clientMode=false;
    private int connectPort=8888;

    private boolean enableRemoteConsole=true;
    private int remoteConsolePort=8888;

    private String containerName;

    private boolean enableLogOutput=true;

    public StartConfig() {
    }

    public String getLocalConsolePath() {
        return localConsolePath;
    }

    public void setLocalConsolePath(String localConsolePath) {
        this.localConsolePath = localConsolePath;
    }

    public int getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(int connectPort) {
        this.connectPort = connectPort;
    }

    public boolean isLocalConsoleMode() {
        return localConsoleMode;
    }

    public void setLocalConsoleMode(boolean localConsoleMode) {
        this.localConsoleMode = localConsoleMode;
    }

    public boolean isClientMode() {
        return clientMode;
    }

    public void setClientMode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    public boolean isEnableRemoteConsole() {
        return enableRemoteConsole;
    }

    public void setEnableRemoteConsole(boolean enableRemoteConsole) {
        this.enableRemoteConsole = enableRemoteConsole;
    }

    public int getRemoteConsolePort() {
        return remoteConsolePort;
    }

    public void setRemoteConsolePort(int remoteConsolePort) {
        this.remoteConsolePort = remoteConsolePort;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public boolean isEnableLogOutput() {
        return enableLogOutput;
    }

    public void setEnableLogOutput(boolean enableLogOutput) {
        this.enableLogOutput = enableLogOutput;
    }

    @Override
    public String toString() {
        return "StartConfig{" +
                "localConsoleMode=" + localConsoleMode +
                ", localConsolePath='" + localConsolePath + '\'' +
                ", clientMode=" + clientMode +
                ", connectPort=" + connectPort +
                ", enableRemoteConsole=" + enableRemoteConsole +
                ", remoteConsolePort=" + remoteConsolePort +
                ", containerName='" + containerName + '\'' +
                ", enableLogOutput=" + enableLogOutput +
                '}';
    }
}
