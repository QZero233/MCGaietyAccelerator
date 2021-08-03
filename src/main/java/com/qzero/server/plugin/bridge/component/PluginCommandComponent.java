package com.qzero.server.plugin.bridge.component;

import com.qzero.server.console.commands.ConsoleCommand;

import java.util.HashMap;
import java.util.Map;

public interface PluginCommandComponent {

    default String getCommandNamePrefix(){return "";}

    default Map<String, ConsoleCommand> getPluginCommands(){
        return new HashMap<>();
    }

}
