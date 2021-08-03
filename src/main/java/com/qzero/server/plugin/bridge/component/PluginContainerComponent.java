package com.qzero.server.plugin.bridge.component;

import com.qzero.server.runner.MinecraftServerContainer;

import java.util.HashMap;
import java.util.Map;

public interface PluginContainerComponent {

    default Map<String, MinecraftServerContainer> getContainer(){
        return new HashMap<>();
    }

}
