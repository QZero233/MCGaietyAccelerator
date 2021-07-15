package com.qzero.server.plugin;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.api.PluginCommand;
import com.qzero.server.plugin.api.PluginEntry;
import com.qzero.server.plugin.api.PluginOperateHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalPluginManager {

    private Map<String, PluginEntry> pluginMap=new HashMap<>();

    private static GlobalPluginManager instance;

    private PluginOperateHelper helper;

    private GlobalPluginManager(){
        //TODO initialize helper
    }

    public static GlobalPluginManager getInstance(){
        if(instance==null)
            instance=new GlobalPluginManager();
        return instance;
    }

    public void loadPlugins(){
        //TODO LOAD PLUGINS FROM FILE SYSTEM
    }

    public void applyPlugin(String pluginName){
        PluginEntry plugin=pluginMap.get(pluginName);
        if(plugin==null)
            throw new IllegalArgumentException(String.format("Plugin named %s does not exist", pluginName));

        //Load commands
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
        List<PluginCommand> commandList=plugin.getPluginCommands();
        for(PluginCommand command:commandList){
            executor.addCommand(command.getCommandName(), new ConsoleCommand() {
                @Override
                public int getCommandParameterCount() {
                    return command.getParameterCount();
                }

                @Override
                public boolean needServerSelected() {
                    return command.needServerSelected();
                }

                @Override
                public String execute(String[] commandParts, String fullCommand, ServerCommandContext context) {
                    return command.execute(commandParts,fullCommand,context,helper);
                }
            });
        }

        //Register listeners


    }

    public void unapplyPlugin(String pluginName){

    }

}
