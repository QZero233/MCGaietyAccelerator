package com.qzero.server.plugin.bridge;

import com.qzero.server.runner.ServerOutputListener;

import java.util.ArrayList;
import java.util.List;

public interface PluginEntry {

    void initializePluginCommandsAndListeners();

    default String getCommandNamePrefix(){return "";}

    default List<PluginCommand> getPluginCommands(){
        return new ArrayList<>();
    }

    default List<ServerOutputListener> getPluginListeners(){
        return new ArrayList<>();
    }

    void onPluginLoaded();

    void onPluginUnloaded();

}
