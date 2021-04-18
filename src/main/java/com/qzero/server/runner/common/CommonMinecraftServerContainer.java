package com.qzero.server.runner.common;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.minecraft.MinecraftServerConfiguration;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommonMinecraftServerContainer implements MinecraftServerContainer {

    private Map<String, MinecraftServerOperator> serverOperatorMap=new HashMap<>();

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public MinecraftServerOperator getServerOperator(String serverName){
        MinecraftServerOperator operator;
        if(!serverOperatorMap.containsKey(serverName)){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getServerConfigurationManager().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new MinecraftServerNotFoundException(serverName,"get server operator");

            operator=new CommonMinecraftServerOperator(configuration);
            serverOperatorMap.put(serverName,operator);
        }else{
            operator=serverOperatorMap.get(serverName);
        }

        return operator;
    }

}
