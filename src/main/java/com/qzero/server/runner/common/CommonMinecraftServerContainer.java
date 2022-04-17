package com.qzero.server.runner.common;

import com.qzero.server.SpringUtil;
import com.qzero.server.config.MinecraftServerConfig;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerOperator;
import com.qzero.server.service.MinecraftConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommonMinecraftServerContainer implements MinecraftServerContainer {

    private Map<String, MinecraftServerOperator> serverOperatorMap=new HashMap<>();

    private Logger log= LoggerFactory.getLogger(getClass());

    private MinecraftConfigService minecraftConfigService;

    public CommonMinecraftServerContainer() {
        minecraftConfigService= SpringUtil.getBean(MinecraftConfigService.class);
    }

    @Override
    public MinecraftServerOperator getServerOperator(String serverName){
        MinecraftServerOperator operator;
        if(!serverOperatorMap.containsKey(serverName)){
            if(!minecraftConfigService.isServerExist(serverName))
                throw new MinecraftServerNotFoundException(serverName,"get server operator");

            operator=new CommonMinecraftServerOperator(serverName);
            serverOperatorMap.put(serverName,operator);
        }else{
            operator=serverOperatorMap.get(serverName);
        }

        return operator;
    }

}
