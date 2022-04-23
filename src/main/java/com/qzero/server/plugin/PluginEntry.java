package com.qzero.server.plugin;

public interface PluginEntry {
    void onPluginLoaded(PluginComponentRegistry registry);

    void onPluginUnloaded(PluginComponentRegistry registry);
}
