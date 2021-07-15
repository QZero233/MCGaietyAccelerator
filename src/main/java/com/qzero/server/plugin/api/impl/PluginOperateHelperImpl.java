package com.qzero.server.plugin.api.impl;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.plugin.api.PluginOperateHelper;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.runner.MinecraftServerOperator;

public class PluginOperateHelperImpl implements PluginOperateHelper {
    @Override
    public MinecraftServerOperator getServerOperator(String serverName) {
        return MinecraftServerContainerSession.getInstance().getCurrentContainer().getServerOperator(serverName);
    }

    @Override
    public GlobalConfigurationManager getConfigManager() {
        return GlobalConfigurationManager.getInstance();
    }
}
