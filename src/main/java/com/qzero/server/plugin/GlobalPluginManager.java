package com.qzero.server.plugin;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.bridge.PluginCommand;
import com.qzero.server.plugin.bridge.PluginEntry;
import com.qzero.server.plugin.bridge.PluginOperateHelper;
import com.qzero.server.plugin.bridge.impl.PluginOperateHelperImpl;
import com.qzero.server.plugin.loader.PluginLoader;
import com.qzero.server.plugin.loader.PluginLoaderFactory;
import com.qzero.server.runner.MinecraftServerOutputProcessCenter;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalPluginManager {

    public static final String PLUGIN_ROOT_PATH="plugins/";

    private Map<String, PluginEntry> pluginMap=new HashMap<>();

    private static GlobalPluginManager instance;

    private PluginOperateHelper helper;

    private Logger log= LoggerFactory.getLogger(getClass());

    private GlobalPluginManager(){
        helper=new PluginOperateHelperImpl();
    }

    public static GlobalPluginManager getInstance(){
        if(instance==null)
            instance=new GlobalPluginManager();
        return instance;
    }

    public void scanAndLoadAutoLoadPlugins() throws IOException {
        File autoLoadListFile=new File(PLUGIN_ROOT_PATH+"autoLoadList.txt");
        if(!autoLoadListFile.exists())
            return;
        byte[] buf= StreamUtils.readFile(autoLoadListFile);
        String autoLoadListString=new String(buf);
        String[] autoLoadPluginNames=autoLoadListString.split(",");

        for(String pluginName:autoLoadPluginNames){
            try {
                loadPlugin(pluginName);
                log.debug("Loaded plugin "+pluginName);
            }catch (Exception e){
                log.error("Failed to load plugin "+pluginName);
            }
        }
    }

    public PluginEntry loadPluginFromFileSystem(String pluginName){
        File pluginJar=new File(PLUGIN_ROOT_PATH+pluginName+".jar");
        File pluginXml=new File(PLUGIN_ROOT_PATH+pluginName+".xml");

        PluginLoaderFactory.PluginFileType fileType;

        if(pluginJar.exists()){
            fileType= PluginLoaderFactory.PluginFileType.JAR;
        }else if(pluginXml.exists()){
            fileType= PluginLoaderFactory.PluginFileType.XML;
        }else{
            throw new IllegalArgumentException(String.format("Plugin named %s is neither a jar plugin nor a xml plugin",
                    pluginName));
        }

        PluginLoader loader=PluginLoaderFactory.getPluginLoader(fileType);

        PluginEntry pluginEntry=null;
        if(fileType == PluginLoaderFactory.PluginFileType.JAR){
            pluginEntry=loader.loadPlugin(pluginJar);
        }else if(fileType == PluginLoaderFactory.PluginFileType.XML){
            pluginEntry=loader.loadPlugin(pluginXml);
        }

        return pluginEntry;
    }

    public void loadPlugin(String pluginName){
        if(pluginMap.containsKey(pluginName))
            throw new IllegalArgumentException(String.format("Plugin named %s has already been loaded",
                    pluginName));

        PluginEntry plugin=loadPluginFromFileSystem(pluginName);
        plugin.initializePluginCommandsAndListeners();

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
                    try {
                        return command.execute(commandParts,fullCommand,context,helper);
                    }catch (NoSuchMethodError e){
                        return String.format("Plugin %s is using outdated api,failed to execute command %s",
                                pluginName,commandNamePrefix+command.getCommandName());
                    }

                }
            });
        }

        //Register listeners
        MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
        List<ServerOutputListener> listenerList=plugin.getPluginListeners();
        for(ServerOutputListener listener:listenerList){
            processCenter.registerOutputListener(listener);
        }

        plugin.onPluginLoaded();

        pluginMap.put(pluginName,plugin);
    }

    public void unloadPlugin(String pluginName) {
        PluginEntry plugin=pluginMap.get(pluginName);
        if(plugin==null)
            throw new IllegalArgumentException(String.format("Plugin named %s is not loaded", pluginName));

        //Unload commands
        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
        List<PluginCommand> commandList=plugin.getPluginCommands();
        String commandNamePrefix=plugin.getCommandNamePrefix();
        for(PluginCommand command:commandList){
            executor.unloadCommand(commandNamePrefix+command.getCommandName());
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
