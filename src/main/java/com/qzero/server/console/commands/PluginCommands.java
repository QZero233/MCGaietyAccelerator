package com.qzero.server.console.commands;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.plugin.GlobalPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    private GlobalPluginManager globalPluginManager=GlobalPluginManager.getInstance();

    @CommandMethod(commandName = "load_plugin",needServerSelected = false,parameterCount = 1)
    private String loadPlugin(String[] commandParts, String commandLine, ServerCommandContext context){
        String pluginName=commandParts[1];
        try {
            globalPluginManager.loadPlugin(pluginName);
            return "Loaded plugin "+pluginName;
        }catch (Exception e){
            log.error("Failed to load plugin "+pluginName,e);
            return "Failed to load plugin "+pluginName;
        }
    }

    @CommandMethod(commandName = "unload_plugin",needServerSelected = false,parameterCount = 1)
    private String unloadPlugin(String[] commandParts, String commandLine, ServerCommandContext context){
        String pluginName=commandParts[1];
        try {
            globalPluginManager.unloadPlugin(pluginName);
            return "Unloaded plugin "+pluginName;
        }catch (Exception e){
            log.error("Failed to unload plugin "+pluginName,e);
            return "Failed to unload plugin "+pluginName;
        }
    }

}
