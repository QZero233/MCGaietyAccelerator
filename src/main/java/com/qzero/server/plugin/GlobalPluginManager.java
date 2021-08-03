package com.qzero.server.plugin;

import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.bridge.PluginEntry;
import com.qzero.server.plugin.bridge.component.PluginCommandComponent;
import com.qzero.server.plugin.bridge.component.PluginContainerComponent;
import com.qzero.server.plugin.bridge.component.PluginListenerComponent;
import com.qzero.server.plugin.loader.PluginLoader;
import com.qzero.server.plugin.loader.PluginLoaderFactory;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerContainerSession;
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
import java.util.Set;

public class GlobalPluginManager {

    public static final String PLUGIN_ROOT_PATH="plugins/";

    private Map<String, PluginEntry> pluginMap=new HashMap<>();

    private static GlobalPluginManager instance;


    private Logger log= LoggerFactory.getLogger(getClass());

    private GlobalPluginManager(){

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

        if(autoLoadListString==null || autoLoadListString.equals(""))
            return;

        String[] autoLoadPluginNames=autoLoadListString.split(",");

        for(String pluginName:autoLoadPluginNames){
            try {
                loadPlugin(pluginName);
                log.debug("Loaded plugin "+pluginName);
            }catch (Exception e){
                log.error("Failed to load plugin "+pluginName,e);
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

        pluginMap.put(pluginName,plugin);
    }

    public void unloadPlugin(String pluginName) {
        PluginEntry plugin=pluginMap.get(pluginName);
        if(plugin==null)
            throw new IllegalArgumentException(String.format("Plugin named %s is not loaded", pluginName));

        Map<String,Object> pluginComponents=plugin.getPluginComponents();

        //Unload commands
        if(pluginComponents.containsKey("command")){
            PluginCommandComponent commandComponent= (PluginCommandComponent) pluginComponents.get("command");

            ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

            Map<String,ConsoleCommand> commandMap=commandComponent.getPluginCommands();
            Set<String> keySet=commandMap.keySet();
            String commandNamePrefix=commandComponent.getCommandNamePrefix();
            for(String commandName:keySet){
                executor.unloadCommand(commandNamePrefix+commandName);
            }
        }

        //Unload listeners
        if(pluginComponents.containsKey("listener")){
            PluginListenerComponent listenerComponent= (PluginListenerComponent) pluginComponents.get("listener");

            MinecraftServerOutputProcessCenter processCenter=MinecraftServerOutputProcessCenter.getInstance();
            List<ServerOutputListener> listenerList=listenerComponent.getPluginListeners();
            for(ServerOutputListener listener:listenerList){
                processCenter.unregisterOutputListener(listener.getListenerId());
            }
        }

        plugin.onPluginUnloaded();
    }

}
