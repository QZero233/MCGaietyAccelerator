package com.qzero.server.plugin;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.bridge.PluginCommand;
import com.qzero.server.plugin.bridge.PluginEntry;
import com.qzero.server.plugin.bridge.PluginOperateHelper;
import com.qzero.server.plugin.bridge.PluginOutputListener;
import com.qzero.server.plugin.bridge.impl.PluginOperateHelperImpl;
import com.qzero.server.plugin.loader.PluginLoader;
import com.qzero.server.plugin.loader.PluginLoaderFactory;
import com.qzero.server.runner.MinecraftServerOutputProcessCenter;
import com.qzero.server.runner.ServerOutputListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalPluginManager {

    public static final String PLUGIN_ROOT_PATH="plugins/";

    private Map<String, PluginEntry> pluginMap=new HashMap<>();

    private static GlobalPluginManager instance;

    private PluginOperateHelper helper;

    private GlobalPluginManager(){
        helper=new PluginOperateHelperImpl();
    }

    public static GlobalPluginManager getInstance(){
        if(instance==null)
            instance=new GlobalPluginManager();
        return instance;
    }

    //TODO 实现插件动态加载，即只有当要用时才从文件系统中加载成entry，就不一开始就加载完了
    public void loadPlugins(){
        File rootPath=new File(PLUGIN_ROOT_PATH);
        File[] fileList=rootPath.listFiles();

        for(File file:fileList){
            if(!file.isFile())
                continue;

            String name=file.getName();
            PluginLoader loader=PluginLoaderFactory.getPluginLoader(name);
            PluginEntry plugin=loader.loadPlugin(file);

            if(pluginMap.containsKey(plugin.getPluginName()))
                throw new IllegalArgumentException(String.format("Plugin named %s has already been loaded",
                        plugin.getPluginName()));

            plugin.initializePluginCommandsAndListeners();
            pluginMap.put(plugin.getPluginName(),plugin);
        }
    }

    public void applyPlugin(String pluginName){
        PluginEntry plugin=pluginMap.get(pluginName);
        if(plugin==null)
            throw new IllegalArgumentException(String.format("Plugin named %s does not exist", pluginName));

        //Load commands
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
        List<PluginCommand> commandList=plugin.getPluginCommands();
        String commandNamePrefix=plugin.getCommandNamePrefix();
        for(PluginCommand command:commandList){
            executor.addCommand(commandNamePrefix+command.getCommandName(), new ConsoleCommand() {
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
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        List<PluginOutputListener> listenerList=plugin.getPluginListeners();
        for(PluginOutputListener listener:listenerList){
            processCenter.registerOutputListener(new ServerOutputListener() {
                @Override
                public String getListenerId() {
                    return listener.getListenerId();
                }

                @Override
                public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
                    PluginOutputListener.OutputType outputTypeDst=null;
                    switch (outputType){
                        case TYPE_NORMAL:
                            outputTypeDst=PluginOutputListener.OutputType.TYPE_NORMAL;
                            break;
                        case TYPE_ERROR:
                            outputTypeDst= PluginOutputListener.OutputType.TYPE_ERROR;
                            break;
                    }

                    listener.receivedOutputLine(serverName,outputLine,outputTypeDst);
                }

                @Override
                public void receivedServerEvent(String serverName, ServerEvent event) {
                    PluginOutputListener.ServerEvent serverEventDst=null;
                    switch (event){
                        case SERVER_STARTED:
                            serverEventDst= PluginOutputListener.ServerEvent.SERVER_STARTED;
                            break;
                        case SERVER_STOPPED:
                            serverEventDst= PluginOutputListener.ServerEvent.SERVER_STOPPED;
                            break;
                        case SERVER_STARTING:
                            serverEventDst= PluginOutputListener.ServerEvent.SERVER_STARTING;
                            break;
                    }

                    listener.receivedServerEvent(serverName,serverEventDst);
                }

                @Override
                public void receivedPlayerEvent(String serverName, String playerName, PlayerEvent event) {
                    PluginOutputListener.PlayerEvent playerEventDst=null;
                    switch (event){
                        case JOIN:
                            playerEventDst= PluginOutputListener.PlayerEvent.JOIN;
                            break;
                    }

                    listener.receivedPlayerEvent(serverName,playerName,playerEventDst);
                }
            });
        }

        plugin.onPluginApplied();
    }

    public void unapplyPlugin(String pluginName) {
        PluginEntry plugin=pluginMap.get(pluginName);
        if(plugin==null)
            throw new IllegalArgumentException(String.format("Plugin named %s does not exist", pluginName));

        //Unload commands
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
        List<PluginCommand> commandList=plugin.getPluginCommands();
        String commandNamePrefix=plugin.getCommandNamePrefix();
        for(PluginCommand command:commandList){
            executor.unloadCommand(commandNamePrefix+command.getCommandName());
        }

        //Unload listeners
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        List<PluginOutputListener> listenerList=plugin.getPluginListeners();
        for(PluginOutputListener listener:listenerList){
            processCenter.unregisterOutputListener(listener.getListenerId());
        }

        plugin.onPluginUnapplied();
    }

}
