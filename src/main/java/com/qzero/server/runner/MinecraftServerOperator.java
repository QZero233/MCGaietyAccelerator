package com.qzero.server.runner;

public interface MinecraftServerOperator {

    boolean checkServerEnvironment();

    void startServer() throws Exception;
    void stopServer();
    void forceStopServer();
    MinecraftRunner.ServerStatus getServerStatus();

    void sendCommand(String commandLine);

    void registerOutputListener(ServerOutputListener listener);
    void unregisterOutputListener(String listenerId);

}
