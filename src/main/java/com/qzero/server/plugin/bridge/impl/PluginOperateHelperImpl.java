package com.qzero.server.plugin.bridge.impl;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.plugin.bridge.PluginMinecraftServerOperator;
import com.qzero.server.plugin.bridge.PluginOperateHelper;
import com.qzero.server.runner.MinecraftRunner;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.runner.MinecraftServerOperator;
import com.qzero.server.runner.ServerOutputListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginOperateHelperImpl implements PluginOperateHelper {

    private GlobalConfigurationManager globalConfigurationManager=GlobalConfigurationManager.getInstance();

    private Map<String,PluginMinecraftServerOperator> serverOperatorMap=new HashMap<>();

    @Override
    public PluginMinecraftServerOperator getServerOperator(String serverName) {
        if(serverOperatorMap.containsKey(serverName))
            return serverOperatorMap.get(serverName);

        MinecraftServerOperator operator=MinecraftServerContainerSession.getInstance().getCurrentContainer().getServerOperator(serverName);
        PluginMinecraftServerOperator pluginOperator= new PluginMinecraftServerOperator() {
            @Override
            public boolean checkServerEnvironment() {
                return operator.checkServerEnvironment();
            }

            @Override
            public void startServer() throws IOException {
                operator.startServer();
            }

            @Override
            public void stopServer() {
                operator.stopServer();
            }

            @Override
            public void forceStopServer() {
                operator.forceStopServer();
            }

            @Override
            public ServerStatus getServerStatus() {
                MinecraftRunner.ServerStatus serverStatus= operator.getServerStatus();
                ServerStatus serverStatusDst=null;
                switch (serverStatus){
                    case RUNNING:
                        serverStatusDst=ServerStatus.RUNNING;
                        break;
                    case STOPPED:
                        serverStatusDst=ServerStatus.STOPPED;
                        break;
                    case STARTING:
                        serverStatusDst=ServerStatus.STARTING;
                        break;
                }

                return serverStatusDst;
            }

            @Override
            public void sendCommand(String commandLine) {
                operator.sendCommand(commandLine);
            }

            @Override
            public void registerOutputListener(ServerOutputListener listener) {
                operator.registerOutputListener(listener);
            }

            @Override
            public void unregisterOutputListener(String listenerId) {
                operator.unregisterOutputListener(listenerId);
            }
        };

        serverOperatorMap.put(serverName,pluginOperator);
        return pluginOperator;
    }



}
