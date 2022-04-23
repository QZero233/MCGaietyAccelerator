package com.qzero.server.plugin;

import java.util.HashSet;
import java.util.Set;

public class PluginComponentRecord {

    private String pluginName;
    private Set<String> commandNames=new HashSet<>();
    private Set<String> listenerIds=new HashSet<>();

    public void addCommandName(String commandName){
        commandNames.add(commandName);
    }

    public void removeCommandName(String commandName){
        commandNames.remove(commandName);
    }

    public void addListenerId(String listenerId){
        listenerIds.add(listenerId);
    }

    public void removeListenerId(String listenerId){
        listenerIds.remove(listenerId);
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public Set<String> getCommandNames() {
        return commandNames;
    }

    public void setCommandNames(Set<String> commandNames) {
        this.commandNames = commandNames;
    }

    public Set<String> getListenerIds() {
        return listenerIds;
    }

    public void setListenerIds(Set<String> listenerIds) {
        this.listenerIds = listenerIds;
    }
}
