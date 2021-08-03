package com.qzero.server.plugin.bridge;

import java.util.Map;

public interface PluginEntry {

    void initializePluginComponents();

    /**
     * The key can be
     * -command
     * -listener
     * -container
     * @return
     */
    Map<String,Object> getPluginComponents();

    void onPluginLoaded();

    void onPluginUnloaded();

}
