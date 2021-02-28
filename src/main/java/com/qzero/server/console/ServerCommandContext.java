package com.qzero.server.console;

public class ServerCommandContext {

    private String currentServer=null;

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }


    @Override
    public String toString() {
        return "ServerCommandContext{" +
                "currentServer='" + currentServer + '\'' +
                '}';
    }
}
