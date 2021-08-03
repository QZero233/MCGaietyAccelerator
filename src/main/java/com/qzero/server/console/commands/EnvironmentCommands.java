package com.qzero.server.console.commands;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.minecraft.MinecraftServerConfiguration;
import com.qzero.server.console.ServerCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class EnvironmentCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    @CommandMethod(commandName = "select",needServerSelected = false,parameterCount = 1)
    private String select(String[] commandParts, String commandLine, ServerCommandContext context){
        String serverName=commandParts[1];
        if(GlobalConfigurationManager.getInstance().getServerConfigurationManager().getMinecraftServerConfig(serverName)==null)
            return String.format("Failed to select, server named %s does not exist", serverName);

        context.setCurrentServer(serverName);
        return "Selected server "+serverName;
    }

    @CommandMethod(commandName = "current_server")
    private String currentServer(String[] commandParts, String commandLine, ServerCommandContext context){
        return context.getCurrentServer();
    }

    @CommandMethod(commandName = "show_all_servers",needServerSelected = false)
    private String showAllServers(String[] commandParts, String commandLine, ServerCommandContext context){
        Map<String, MinecraftServerConfiguration> configurationMap=GlobalConfigurationManager.getInstance().getServerConfigurationManager().getMcServers();
        if(configurationMap.isEmpty())
            return "There has been no server yet now";

        Set<String> nameSet=configurationMap.keySet();
        StringBuffer stringBuffer=new StringBuffer();
        for(String name:nameSet){
            stringBuffer.append(name);
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    @CommandMethod(commandName = "memory_status",needServerSelected = false)
    private String getServerMemoryStatus(String[] commandParts, String commandLine, ServerCommandContext context){
        Runtime runtime=Runtime.getRuntime();
        StringBuffer result=new StringBuffer();
        result.append(String.format("Free Memory:%.2fMiB\n", (double)runtime.freeMemory()/(1024*1024)));
        result.append(String.format("Max memory:%.2fMiB\n", (double)runtime.maxMemory()/(1024*1024)));
        result.append(String.format("Total memory:%.2fMiB\n", (double)runtime.totalMemory()/(1024*1024)));
        return result.toString();
    }

}
