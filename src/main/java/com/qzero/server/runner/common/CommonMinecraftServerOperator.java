package com.qzero.server.runner.common;

import com.qzero.server.SpringUtil;
import com.qzero.server.config.MinecraftServerConfig;
import com.qzero.server.exception.MinecraftServerStatusException;
import com.qzero.server.runner.MinecraftRunner;
import com.qzero.server.runner.MinecraftServerOperator;
import com.qzero.server.runner.MinecraftServerOutputProcessCenter;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.service.MinecraftConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommonMinecraftServerOperator implements MinecraftServerOperator {

    private Logger log= LoggerFactory.getLogger(getClass());

    protected MinecraftRunner runner;
    protected String serverName;

    protected MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();

    private MinecraftConfigService minecraftConfigService;

    private MinecraftServerConfig config;

    public CommonMinecraftServerOperator(String serverName) {
        this.serverName=serverName;
        runner=new MinecraftRunner(serverName);
        minecraftConfigService= SpringUtil.getBean(MinecraftConfigService.class);

        try {
            config=minecraftConfigService.getConfig(serverName);
        } catch (Exception e) {
            log.error("Failed to get server config for server "+serverName,e);
        }
    }

    @Override
    public boolean checkServerEnvironment() {
        try {
            minecraftConfigService.checkIfMinecraftServerRunnable(config);
            return true;
        } catch (Exception e) {
            log.error(String.format("Check server environment for server named %s failed", serverName),e);
            return false;
        }
    }

    @Override
    public void startServer() throws Exception {
        minecraftConfigService.checkIfMinecraftServerRunnable(config);
        if(runner.getServerStatus()== MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"stopped","running","start server again");

        runner.startServer(config);
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
        processCenter.registerOutputListener(listener);
    }

    @Override
    public void unregisterOutputListener(String listenerId) {
        processCenter.unregisterOutputListener(listenerId);
    }

}
