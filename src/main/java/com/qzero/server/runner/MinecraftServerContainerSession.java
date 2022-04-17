package com.qzero.server.runner;

import com.qzero.server.runner.common.CommonMinecraftServerContainer;

import java.util.HashMap;
import java.util.Map;

public class MinecraftServerContainerSession {

    private static MinecraftServerContainerSession instance;

    private Map<String,MinecraftServerContainer> containerMap=new HashMap<>();

    private MinecraftServerContainer currentContainer;

    //Register some containers
    static {
        getInstance().loadContainer("common",new CommonMinecraftServerContainer());
    }

    private MinecraftServerContainerSession(){

    }

    public static MinecraftServerContainerSession getInstance() {
        if(instance==null)
            instance=new MinecraftServerContainerSession();
        return instance;
    }

    public void initCurrentContainer(String containerName){
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
