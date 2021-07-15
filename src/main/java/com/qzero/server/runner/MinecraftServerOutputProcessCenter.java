package com.qzero.server.runner;

import com.qzero.server.console.InGameCommandContextSwitchListener;
import com.qzero.server.console.InGameCommandListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MinecraftServerOutputProcessCenter {

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
                outputListenerMap.get(key).receivedOutputLine(serverName, output, type);
            }
        }
    }

    public void broadcastServerEvent(String serverName,ServerOutputListener.ServerEvent event) {
        synchronized (outputListenerMap) {
            Set<String> keySet = outputListenerMap.keySet();

            Set<String> removeSet=new HashSet<>();

            for (String key : keySet) {
                outputListenerMap.get(key).receivedServerEvent(serverName, event);
                if(outputListenerMap.get(key).isSingleTimeEventListener())
                    removeSet.add(key);
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
                outputListenerMap.get(key).receivedPlayerEvent(serverName,playerName,event);
            }
        }
    }

}
