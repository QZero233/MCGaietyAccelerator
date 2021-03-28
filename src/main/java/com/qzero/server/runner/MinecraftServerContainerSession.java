package com.qzero.server.runner;

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
                break;
            case SINGLE_PORT:
                break;
        }
    }

    public MinecraftServerContainer getCurrentContainer(){
        return currentContainer;
    }

}
