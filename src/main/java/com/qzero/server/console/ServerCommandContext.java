package com.qzero.server.console;

public class ServerCommandContext {

    public enum ExecuteEnvType{
        LOCAL_CONSOLE,
        REMOTE_CONSOLE,
        IN_GAME
    };

    private String operatorId;
    private String currentServer=null;
    private ExecuteEnvType envType=null;

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }

    public ExecuteEnvType getEnvType() {
        return envType;
    }

    public void setEnvType(ExecuteEnvType envType) {
        this.envType = envType;
    }

    @Override
    public String toString() {
        return "ServerCommandContext{" +
                "operatorId='" + operatorId + '\'' +
                ", currentServer='" + currentServer + '\'' +
                ", envType=" + envType +
                '}';
    }
}
