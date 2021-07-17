package com.qzero.server.config.minecraft;

import com.qzero.server.config.IConfigurationManager;

import java.io.IOException;
import java.util.Map;

public interface IMinecraftServerConfigurationManager extends IConfigurationManager {
    Map<String, MinecraftServerConfiguration> getMcServers();

    void updateMinecraftServerConfig(String serverName, String key, String value) throws IOException;

    MinecraftServerConfiguration getMinecraftServerConfig(String serverName);
}
