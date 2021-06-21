package com.qzero.server.console;

import java.util.HashMap;
import java.util.Map;

public class GlobalOperatorContextContainer {

    private static GlobalOperatorContextContainer instance;

    private Map<String,ServerCommandContext> contextMap=new HashMap<>();

    private GlobalOperatorContextContainer(){

    }

    public static GlobalOperatorContextContainer getInstance(){
        if(instance==null)
            instance=new GlobalOperatorContextContainer();
        return instance;
    }

    public ServerCommandContext getContext(String operatorName){
        return contextMap.get(operatorName);
    }

    public void saveContext(String operatorName,ServerCommandContext context){
        contextMap.put(operatorName,context);
    }

    public void removeContext(String operatorName){
        contextMap.remove(operatorName);
    }

}
