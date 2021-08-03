package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.mcga.MCGAConfiguration;
import com.qzero.server.runner.common.CommonMinecraftServerContainer;

import java.util.HashMap;
import java.util.Map;

public class MinecraftServerContainerSession {

    private static MinecraftServerContainerSession instance;

    private Map<String,MinecraftServerContainer> containerMap=new HashMap<>();

    private MinecraftServerContainer currentContainer;

    private MinecraftServerContainerSession(){

    }

    public static MinecraftServerContainerSession getInstance() {
        if(instance==null)
            instance=new MinecraftServerContainerSession();
        return instance;
    }

    public void initContainer(){
        MCGAConfiguration managerConfiguration= GlobalConfigurationManager.getInstance().getMcgaConfigurationManager().getMcgaConfiguration();
        String containerName=managerConfiguration.getContainerName();

        if(containerName==null){
            currentContainer=new CommonMinecraftServerContainer();
            containerMap=null;
            return;
        }

        if(!containerMap.containsKey(containerName))
            throw new IllegalArgumentException(String.format("Minecraft server container named %s does not exist", containerName));

        currentContainer=containerMap.get(containerName);
        containerMap=null;
    }

    public MinecraftServerContainer getCurrentContainer(){
        return currentContainer;
    }

    public void loadContainer(String containerName,MinecraftServerContainer container){
        containerMap.put(containerName,container);
    }

}
