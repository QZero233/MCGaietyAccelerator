package com.qzero.server.exception;

public class MinecraftServerStatusException extends IllegalStateException {

    private String serverName;
    private String expectedStatus;
    private String currentStatus;
    private String actionName;

    public MinecraftServerStatusException(String serverName, String expectedStatus, String currentStatus,
                                          String actionName) {
        super(String.format("Server named %s is %s (expected to be %s), failed to execute %s",
                serverName,currentStatus,expectedStatus,actionName));
        this.serverName = serverName;
        this.expectedStatus = expectedStatus;
        this.currentStatus = currentStatus;
        this.actionName=actionName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getExpectedStatus() {
        return expectedStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getActionName() {
        return actionName;
    }
}
