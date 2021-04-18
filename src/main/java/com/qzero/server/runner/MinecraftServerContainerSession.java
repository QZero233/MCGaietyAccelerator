package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.manager.ServerManagerConfiguration;
import com.qzero.server.runner.common.CommonMinecraftServerContainer;
import com.qzero.server.runner.single.SinglePortMinecraftServerContainer;

public class MinecraftServerContainerSession {

    private static MinecraftServerContainerSession instance;

    private MinecraftServerContainer currentContainer;

    public enum ContainerType{
        COMMON,
        SINGLE_PORT
    }

    private MinecraftServerContainerSession(){

    }

    public static MinecraftServerContainerSession getInstance() {
        if(instance==null)
            instance=new MinecraftServerContainerSession();
        return instance;
    }

    public void initContainer(){
        ServerManagerConfiguration managerConfiguration= GlobalConfigurationManager.getInstance().getManagerConfigurationManager().getManagerConfiguration();
        switch (managerConfiguration.getContainerType()){
            case COMMON:
                currentContainer=new CommonMinecraftServerContainer();
                break;
            case SINGLE_PORT:
                currentContainer=new SinglePortMinecraftServerContainer(managerConfiguration.getSinglePortInInt());
                break;
        }
    }

    public MinecraftServerContainer getCurrentContainer(){
        return currentContainer;
    }

}
