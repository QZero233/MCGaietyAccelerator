package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftEnvironmentChecker;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MinecraftServerContainer {

    private Map<String, MinecraftServerOperator> serverOperatorMap=new HashMap<>();

    private static MinecraftServerContainer instance;

    private Logger log= LoggerFactory.getLogger(getClass());


    private MinecraftServerContainer(){}

    public static MinecraftServerContainer getInstance(){
        if(instance==null)
            instance=new MinecraftServerContainer();
        return instance;
    }

    public boolean checkServer(String serverName){
        MinecraftServerConfiguration configuration=GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);
        if(configuration==null)
            throw new MinecraftServerNotFoundException(serverName,"check server");

        MinecraftEnvironmentChecker checker=new MinecraftEnvironmentChecker(configuration);
        try {
            checker.checkMinecraftServerEnvironment();
            return true;
        } catch (IOException e) {
            log.error(String.format("Check server environment for server named %s failed", serverName),e);
            return false;
        }
    }

    public MinecraftServerOperator getServerOperator(String serverName){
        MinecraftServerOperator operator;
        if(!serverOperatorMap.containsKey(serverName)){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new MinecraftServerNotFoundException(serverName,"get server operator");

            operator=new MinecraftServerOperatorImpl(configuration);
            serverOperatorMap.put(serverName,operator);
        }else{
            operator=serverOperatorMap.get(serverName);
        }

        return operator;
    }

}
