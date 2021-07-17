package com.qzero.server.runner;

import java.io.IOException;

public interface MinecraftServerOperator {

    boolean checkServerEnvironment();

    void startServer() throws IOException;
    void stopServer();
    void forceStopServer();
    MinecraftRunner.ServerStatus getServerStatus();

    void sendCommand(String commandLine);

    void registerOutputListener(ServerOutputListener listener);
    void unregisterOutputListener(String listenerId);

}
