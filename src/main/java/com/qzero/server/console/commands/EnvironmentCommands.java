package com.qzero.server.console.commands;

import com.qzero.server.SpringUtil;
import com.qzero.server.config.MinecraftServerConfig;
import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.service.MinecraftConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnvironmentCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    private MinecraftConfigService minecraftConfigService;

    public EnvironmentCommands() {
        minecraftConfigService= SpringUtil.getBean(MinecraftConfigService.class);
    }

    @CommandMethod(commandName = "select",needServerSelected = false,parameterCount = 1)
    private String select(String[] commandParts, String commandLine, ServerCommandContext context){
        String serverName=commandParts[1];
        if(!minecraftConfigService.isServerExist(serverName))
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
        List<String> serverNameList=minecraftConfigService.getAllServers();
        if(serverNameList.isEmpty())
            return "There has been no server yet now";

        StringBuffer stringBuffer=new StringBuffer();
        for(String name:serverNameList){
            stringBuffer.append(name);
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    @CommandMethod(commandName = "memory_status",needServerSelected = false,minAdminPermission = -1)
    private String getServerMemoryStatus(String[] commandParts, String commandLine, ServerCommandContext context){
        Runtime runtime=Runtime.getRuntime();
        StringBuffer result=new StringBuffer();
        result.append(String.format("Free Memory:%.2fMiB\n", (double)runtime.freeMemory()/(1024*1024)));
        result.append(String.format("Max memory:%.2fMiB\n", (double)runtime.maxMemory()/(1024*1024)));
        result.append(String.format("Total memory:%.2fMiB\n", (double)runtime.totalMemory()/(1024*1024)));
        return result.toString();
    }

    @CommandMethod(commandName = "output",needServerSelected = false)
    private String output(String[] commandParts, String commandLine, ServerCommandContext context){
        String msg=commandLine.replaceFirst("output ","");
        System.out.println(msg);
        return msg;
    }

    @CommandMethod(commandName = "output_start_msg",needServerSelected = false)
    private String outputStartMsg(String[] commandParts, String commandLine, ServerCommandContext context){
        System.out.println("Time elapsed: 3050 ms");
        System.out.println("Done (3.307s)! For help, type \"help\"");
        return "Done (3.307s)! For help, type \"help\"";
    }

    @CommandMethod(commandName = "output_stop_msg",needServerSelected = false)
    private String outputStopMsg(String[] commandParts, String commandLine, ServerCommandContext context){
        System.out.println("ThreadedAnvilChunkStorage (DIM1): All chunks are saved");
        return "ThreadedAnvilChunkStorage (DIM1): All chunks are saved";
    }

}
