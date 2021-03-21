package com.qzero.server.runner;

public interface MinecraftServerOperator {

    void startServer();
    void stopServer();
    void forceStopServer();
    MinecraftRunner.ServerStatus getServerStatus();

    void sendCommand(String commandLine);

    void registerOutputListener(ServerOutputListener listener);
    void unregisterOutputListener(String listenerId);

}
