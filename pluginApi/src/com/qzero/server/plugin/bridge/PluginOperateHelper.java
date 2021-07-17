package com.qzero.server.plugin.bridge;

import com.qzero.server.runner.MinecraftServerOperator;

public interface PluginOperateHelper {

    MinecraftServerOperator getServerOperator(String serverName);

    IAuthorizeConfigurationManager getAuthorizeConfigurationManager();

    IServerEnvironmentConfigurationManager getServerEnvironmentConfigurationManager();

    IServerManagerConfigurationManager getServerManagerConfigurationManager();

    IMinecraftServerConfigurationManager getMinecraftServerConfigurationManager();

}
