package com.qzero.server.plugin.bridge;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.runner.MinecraftServerOperator;

public interface PluginOperateHelper {

    MinecraftServerOperator getServerOperator(String serverName);

    GlobalConfigurationManager getGlobalConfigurationManager();

}