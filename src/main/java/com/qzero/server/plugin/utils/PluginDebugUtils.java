package com.qzero.server.plugin.utils;

import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.bridge.PluginEntry;
import com.qzero.server.runner.MinecraftServerOutputProcessCenter;
import com.qzero.server.runner.ServerOutputListener;
import com.sun.corba.se.impl.activation.ServerMain;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginDebugUtils {

    public static void loadPluginAndLaunchConsoleToDebug(PluginEntry pluginEntry){
        ServerMain.main(new String[]{});
        loadPlugin(pluginEntry);
    }

    public static void loadPlugin(PluginEntry plugin){
        plugin.initializePluginCommandsAndListeners();

        //Load commands
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

        Map<String, ConsoleCommand> commandMap=plugin.getPluginCommands();
        Set<String> keySet=commandMap.keySet();
        for(String commandName:keySet){
            ConsoleCommand command=commandMap.get(commandName);
            String commandNamePrefix=plugin.getCommandNamePrefix();

            executor.addCommand(commandNamePrefix+commandName,command);
        }

        //Register listeners
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        List<ServerOutputListener> listenerList=plugin.getPluginListeners();
        for(ServerOutputListener listener:listenerList){
            processCenter.registerOutputListener(listener);
        }

        plugin.onPluginLoaded();
    }

    public static void unloadPlugin(PluginEntry plugin){
        //Unload commands
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

        Map<String,ConsoleCommand> commandMap=plugin.getPluginCommands();
        Set<String> keySet=commandMap.keySet();
        String commandNamePrefix=plugin.getCommandNamePrefix();
        for(String commandName:keySet){
            executor.unloadCommand(commandNamePrefix+commandName);
        }

        //Unload listeners
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        List<ServerOutputListener> listenerList=plugin.getPluginListeners();
        for(ServerOutputListener listener:listenerList){
            processCenter.unregisterOutputListener(listener.getListenerId());
        }

        plugin.onPluginUnloaded();
    }

}
