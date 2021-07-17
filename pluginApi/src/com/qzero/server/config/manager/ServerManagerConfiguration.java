package com.qzero.server.config.manager;

import com.qzero.server.runner.MinecraftServerContainerSession;

public class ServerManagerConfiguration {

    private String serverType;
    private String singlePort;
    private String remoteConsolePort;


    private int singlePortInInt;
    private int remoteConsolePortInInt;
    private boolean enableRemoteConsole;
    private MinecraftServerContainerSession.ContainerType containerType;

    public static final String SERVER_TYPE_COMMON="common";
    public static final String SERVER_TYPE_SINGLE_PORT="singlePort";

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getSinglePort() {
        return singlePort;
    }

    public void setSinglePort(String singlePort) {
        this.singlePort = singlePort;
    }

    public String getRemoteConsolePort() {
        return remoteConsolePort;
    }

    public void setRemoteConsolePort(String remoteConsolePort) {
        this.remoteConsolePort = remoteConsolePort;
    }

    public int getSinglePortInInt() {
        return singlePortInInt;
    }

    public void setSinglePortInInt(int singlePortInInt) {
        this.singlePortInInt = singlePortInInt;
    }

    public int getRemoteConsolePortInInt() {
        return remoteConsolePortInInt;
    }

    public void setRemoteConsolePortInInt(int remoteConsolePortInInt) {
        this.remoteConsolePortInInt = remoteConsolePortInInt;
    }

    public boolean isEnableRemoteConsole() {
        return enableRemoteConsole;
    }

    public void setEnableRemoteConsole(boolean enableRemoteConsole) {
        this.enableRemoteConsole = enableRemoteConsole;
    }

    public MinecraftServerContainerSession.ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(MinecraftServerContainerSession.ContainerType containerType) {
        this.containerType = containerType;
    }

    @Override
    public String toString() {
        return "ServerManagerConfiguration{" +
                "serverType='" + serverType + '\'' +
                ", singlePort='" + singlePort + '\'' +
                ", remoteConsolePort='" + remoteConsolePort + '\'' +
                ", singlePortInInt=" + singlePortInInt +
                ", remoteConsolePortInInt=" + remoteConsolePortInInt +
                ", enableRemoteConsole=" + enableRemoteConsole +
                '}';
    }
}
