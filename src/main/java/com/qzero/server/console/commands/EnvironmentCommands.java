package com.qzero.server.console.commands;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.config.MinecraftServerConfigurator;
import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.runner.MinecraftServerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@CommandConfiguration
public class EnvironmentCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    @CommandMethod(commandName = "select",needServerSelected = false,parameterCount = 1)
    private String select(String[] commandParts, String commandLine, ServerCommandContext context){
        String serverName=commandParts[1];
        if(GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName)==null)
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
        Map<String, MinecraftServerConfiguration> configurationMap=GlobalConfigurationManager.getInstance().getMcServers();
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

    @CommandMethod(commandName = "auto_config",parameterCount = 1)
    private String autoConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        try {
            MinecraftServerConfigurator.configServer(commandParts[1]);
            return "Auto config successfully";
        } catch (IOException e) {
            log.error("Failed to auto config server for "+context.getCurrentServer(),e);
            return "Failed to auto config server for "+context.getCurrentServer();
        }
    }
}
