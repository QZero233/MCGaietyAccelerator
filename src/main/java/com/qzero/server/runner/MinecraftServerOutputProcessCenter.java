package com.qzero.server.runner;

import com.qzero.server.console.InGameCommandContextSwitchListener;
import com.qzero.server.console.InGameCommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MinecraftServerOutputProcessCenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private static MinecraftServerOutputProcessCenter instance;

    private Map<String, ServerOutputListener> outputListenerMap = new HashMap<>();

    private InGameCommandListener commandListener;
    private InGameCommandContextSwitchListener commandContextSwitchListener;

    private MinecraftServerOutputProcessCenter(){
        commandListener=new InGameCommandListener();
        commandContextSwitchListener=new InGameCommandContextSwitchListener();

        registerOutputListener(commandListener);
        registerOutputListener(commandContextSwitchListener);
    }

    public static MinecraftServerOutputProcessCenter getInstance(){
        if(instance==null)
            instance=new MinecraftServerOutputProcessCenter();
        return instance;
    }

    public void registerOutputListener(ServerOutputListener listener) {
        synchronized (outputListenerMap) {
            outputListenerMap.put(listener.getListenerId(), listener);
        }
    }

    public void unregisterOutputListener(String listenerId) {
        synchronized (outputListenerMap) {
            outputListenerMap.remove(listenerId);
        }
    }

    public void broadcastOutput(String serverName,String output, ServerOutputListener.OutputType type) {
        synchronized (outputListenerMap) {

            Set<String> keySet = outputListenerMap.keySet();
            for (String key : keySet) {
                try {
                    outputListenerMap.get(key).receivedOutputLine(serverName, output, type);
                }catch (Exception e){
                    log.error("Failed to handle output line for listener with id "+key,e);
                }
            }
        }
    }

    public void broadcastServerEvent(String serverName,ServerOutputListener.ServerEvent event) {
        synchronized (outputListenerMap) {
            Set<String> keySet = outputListenerMap.keySet();

            Set<String> removeSet=new HashSet<>();

            for (String key : keySet) {
                try {
                    outputListenerMap.get(key).receivedServerEvent(serverName, event);
                    if(outputListenerMap.get(key).isSingleTimeEventListener())
                        removeSet.add(key);
                }catch (Exception e){
                    log.error("Failed to handle server event for listener with id "+key,e);
                }
            }

            for(String key:removeSet){
                outputListenerMap.remove(key);
            }
        }
    }

    public void broadcastPlayerEvent(String serverName,String playerName, ServerOutputListener.PlayerEvent event){
        synchronized (outputListenerMap) {
            Set<String> keySet = outputListenerMap.keySet();
            for (String key : keySet) {
                try {
                    outputListenerMap.get(key).receivedPlayerEvent(serverName,playerName,event);
                }catch (Exception e){
                    log.error("Failed to handle player event for listener with id "+key,e);
                }

            }
        }
    }

}
