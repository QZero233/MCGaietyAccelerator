package com.qzero.server.exception;

public class MinecraftServerNotFoundException extends IllegalArgumentException {

    private String serverName;
    private String actionName;

    public MinecraftServerNotFoundException(String serverName,String actionName) {
        super(String.format("Server named %s does not exist, failed to execute ", serverName, actionName));
        this.serverName = serverName;
        this.actionName=actionName;
    }

    public String getActionName() {
        return actionName;
    }

    public String getServerName() {
        return serverName;
    }

}
