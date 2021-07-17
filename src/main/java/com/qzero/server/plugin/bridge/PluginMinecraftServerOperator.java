package com.qzero.server.plugin.bridge;

import com.qzero.server.runner.ServerOutputListener;

import java.io.IOException;

public interface PluginMinecraftServerOperator {

    enum ServerStatus {
        STARTING,
        RUNNING,
        STOPPED
    }

    boolean checkServerEnvironment();

    void startServer() throws IOException;
    void stopServer();
    void forceStopServer();
    ServerStatus getServerStatus();

    void sendCommand(String commandLine);

    void registerOutputListener(ServerOutputListener listener);
    void unregisterOutputListener(String listenerId);

}
