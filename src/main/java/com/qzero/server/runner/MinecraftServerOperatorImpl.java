package com.qzero.server.runner;

import com.qzero.server.config.MinecraftEnvironmentChecker;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.exception.MinecraftServerStatusException;

import java.io.IOException;

public class MinecraftServerOperatorImpl implements MinecraftServerOperator {

    private MinecraftRunner runner;
    private String serverName;
    private MinecraftServerConfiguration configuration;

    public MinecraftServerOperatorImpl(MinecraftServerConfiguration configuration) {
        this.configuration = configuration;
        serverName=configuration.getServerName();
        runner=new MinecraftRunner(configuration);
    }

    @Override
    public void startServer() throws IOException {
        new MinecraftEnvironmentChecker(configuration).checkMinecraftServerEnvironment();
        if(runner.getServerStatus()== MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"stopped","running","start server again");

        runner.startServer();
    }

    @Override
    public void stopServer() {
        if(runner.getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"running","stopped","stop server");

        runner.stopServer();
    }

    @Override
    public void forceStopServer() {
        runner.forceStopServer();
    }

    @Override
    public MinecraftRunner.ServerStatus getServerStatus() {
        return runner.getServerStatus();
    }

    @Override
    public void sendCommand(String commandLine) {
        if(runner.getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"running","stopped","send command");

        runner.sendCommand(commandLine);
    }

    @Override
    public void registerOutputListener(ServerOutputListener listener) {
        runner.registerOutputListener(listener);
    }

    @Override
    public void unregisterOutputListener(String listenerId) {
        runner.unregisterOutputListener(listenerId);
    }
}
