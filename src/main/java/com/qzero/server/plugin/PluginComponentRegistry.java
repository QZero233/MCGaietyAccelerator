package com.qzero.server.plugin;

import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.runner.MinecraftServerOutputProcessCenter;
import com.qzero.server.runner.ServerOutputListener;

import java.util.HashMap;
import java.util.Map;

public class PluginComponentRegistry {

    private String pluginName;

    private Map<String,PluginComponentRecord> recordMap=new HashMap<>();

    public PluginComponentRegistry(String pluginName) {
        this.pluginName = pluginName;
        if(!recordMap.containsKey(pluginName))
            recordMap.put(pluginName,new PluginComponentRecord());
    }

    public void addCommand(String commandName, ConsoleCommand command){
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
        executor.addCommand(commandName,command);

        recordMap.get(pluginName).addCommandName(commandName);
    }

    public void removeCommand(String commandName){
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
        executor.unloadCommand(commandName);

        recordMap.get(pluginName).removeCommandName(commandName);
    }

    public void addServerOutputListener(ServerOutputListener listener){
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        processCenter.registerOutputListener(listener);

        recordMap.get(pluginName).addListenerId(listener.getListenerId());
    }

    public void removeServerOutputListener(String listenerId){
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        processCenter.unregisterOutputListener(listenerId);

        recordMap.get(pluginName).removeListenerId(listenerId);
    }

    public PluginComponentRecord getRecord(){
        return recordMap.get(pluginName);
    }

    public void resetRecord(){
        recordMap.remove(pluginName);
    }

}
