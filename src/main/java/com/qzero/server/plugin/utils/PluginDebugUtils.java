package com.qzero.server.plugin.utils;

import com.qzero.server.ServerManagerMain;
import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.bridge.PluginEntry;
import com.qzero.server.plugin.bridge.component.PluginCommandComponent;
import com.qzero.server.plugin.bridge.component.PluginContainerComponent;
import com.qzero.server.plugin.bridge.component.PluginListenerComponent;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.runner.MinecraftServerOutputProcessCenter;
import com.qzero.server.runner.ServerOutputListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginDebugUtils {

    public static void loadPluginAndLaunchConsoleToDebug(PluginEntry pluginEntry) throws Exception {
        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();
        configurationManager.loadConfig();

        loadPlugin(pluginEntry);
        ServerManagerMain.main(new String[]{});
    }

    public static void loadPlugin(PluginEntry plugin){
        String pluginName="test";
        plugin.initializePluginComponents();
        Map<String,Object> pluginComponents=plugin.getPluginComponents();

        //Load commands
        if(pluginComponents.containsKey("command")){
            if(!(pluginComponents.get("command") instanceof PluginCommandComponent))
                throw new IllegalArgumentException(String.format("The command component of plugin %s has a wrong type", pluginName));

            PluginCommandComponent commandComponent= (PluginCommandComponent) pluginComponents.get("command");

            ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

            Map<String,ConsoleCommand> commandMap=commandComponent.getPluginCommands();
            Set<String> keySet=commandMap.keySet();
            for(String commandName:keySet){
                ConsoleCommand command=commandMap.get(commandName);
                String commandNamePrefix=commandComponent.getCommandNamePrefix();

                executor.addCommand(commandNamePrefix+commandName,command);
            }
        }

        //Register listeners
        if(pluginComponents.containsKey("listener")){
            if(!(pluginComponents.get("listener") instanceof PluginListenerComponent))
                throw new IllegalArgumentException(String.format("The listener component of plugin %s has a wrong type", pluginName));

            PluginListenerComponent listenerComponent= (PluginListenerComponent) pluginComponents.get("listener");

            MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
            List<ServerOutputListener> listenerList=listenerComponent.getPluginListeners();
            for(ServerOutputListener listener:listenerList){
                processCenter.registerOutputListener(listener);
            }
        }

        //Load containers
        if(pluginComponents.containsKey("container")) {
            if (!(pluginComponents.get("container") instanceof PluginContainerComponent))
                throw new IllegalArgumentException(String.format("The container component of plugin %s has a wrong type", pluginName));

            PluginContainerComponent containerComponent= (PluginContainerComponent) pluginComponents.get("container");

            Map<String, MinecraftServerContainer> containerMap=containerComponent.getContainer();
            Set<String> keySet=containerMap.keySet();

            MinecraftServerContainerSession containerSession=MinecraftServerContainerSession.getInstance();
            for(String key:keySet){
                MinecraftServerContainer container=containerMap.get(key);
                containerSession.loadContainer(key,container);
            }
        }

        plugin.onPluginLoaded();
    }
}
