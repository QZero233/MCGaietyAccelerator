package com.qzero.server.plugin.bridge.impl;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.plugin.bridge.PluginOperateHelper;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.runner.MinecraftServerOperator;

public class PluginOperateHelperImpl implements PluginOperateHelper {

    private MinecraftServerContainer container=MinecraftServerContainerSession.getInstance().getCurrentContainer();

    @Override
    public MinecraftServerOperator getServerOperator(String serverName) {
        return container.getServerOperator(serverName);
    }

    @Override
    public GlobalConfigurationManager getGlobalConfigurationManager() {
        return GlobalConfigurationManager.getInstance();
    }


}
