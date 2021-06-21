package com.qzero.server.plugin;

import com.qzero.server.runner.MinecraftServerOperator;

public interface PluginServerOperateHelper {

    MinecraftServerOperator getServerOperator(String serverName);

}
