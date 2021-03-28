package com.qzero.server.runner;

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

    public void initContainer(ContainerType type){
        switch (type){
            case COMMON:
                currentContainer=new CommonMinecraftServerContainer();
                break;
            case SINGLE_PORT:
                currentContainer=new SinglePortMinecraftServerContainer(8848);
                break;
        }
    }

    public MinecraftServerContainer getCurrentContainer(){
        return currentContainer;
    }

}
