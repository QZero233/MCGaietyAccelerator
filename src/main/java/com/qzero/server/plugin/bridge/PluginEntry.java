package com.qzero.server.plugin.bridge;

import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.runner.ServerOutputListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PluginEntry {

    void initializePluginCommandsAndListeners();

    default String getCommandNamePrefix(){return "";}

    default Map<String,ConsoleCommand> getPluginCommands(){
        return new HashMap<>();
    }

    default List<ServerOutputListener> getPluginListeners(){
        return new ArrayList<>();
    }

    void onPluginLoaded();

    void onPluginUnloaded();

}
