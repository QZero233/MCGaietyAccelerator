package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommonMinecraftServerContainer implements MinecraftServerContainer {

    private Map<String, MinecraftServerOperator> serverOperatorMap=new HashMap<>();

    private Logger log= LoggerFactory.getLogger(getClass());
    public CommonMinecraftServerContainer(){}

    @Override
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
