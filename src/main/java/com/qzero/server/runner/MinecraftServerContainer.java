package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MinecraftServerContainer {

    private Map<String, MinecraftRunner> serverRunnerMap=new HashMap<>();

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
            throw new IllegalArgumentException("String.format(\"[MinecraftServerContainer]Server named %s does not exist\", serverName)");

        return false;
    }

    public void startServer(String serverName){
        MinecraftRunner runner;
        if(!serverRunnerMap.containsKey(serverName)){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new IllegalArgumentException("String.format(\"[MinecraftServerContainer]Server named %s does not exist\", serverName)");

            runner=new MinecraftRunner(configuration);
            serverRunnerMap.put(serverName,runner);
        }else{
            runner=serverRunnerMap.get(serverName);
        }

        if(runner.getServerStatus()== MinecraftRunner.ServerStatus.RUNNING)
            throw new IllegalStateException(String.format("[MinecraftServerContainer]Server named %s is running now, you can not start it again",
                    serverName));

        runner.startServer();
    }

    public void stopServer(String serverName){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null || runner.getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new IllegalStateException(String.format("[MinecraftServerContainer]Server named %s is not running", serverName));

        runner.stopServer();
    }

    public void forceStopServer(String serverName){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null)
            throw new IllegalStateException(String.format("[MinecraftServerContainer]Server named %s has no instance running", serverName));
        runner.forceStopServer();
    }

    public void sendCommand(String serverName,String command){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null || runner.getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new IllegalStateException(String.format("[MinecraftServerContainer]Server named %s is not running", serverName));

        runner.sendCommand(command);
    }

    public void registerOutputListener(String serverName,ServerOutputListener listener){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new IllegalArgumentException("String.format(\"[MinecraftServerContainer]Server named %s does not exist\", serverName)");

            runner=new MinecraftRunner(configuration);
            serverRunnerMap.put(serverName,runner);
        }

        runner.registerOutputListener(listener);
    }

    public void unregisterOutputListener(String serverName,String listenerId){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null)
            throw new IllegalStateException(String.format("[MinecraftServerContainer]Server named %s has no running instance", serverName));

        runner.unregisterOutputListener(listenerId);
    }

    public MinecraftRunner.ServerStatus getServerStatus(String serverName){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null)
            throw new IllegalStateException(String.format("[MinecraftServerContainer]Server named %s has no running instance", serverName));

        return runner.getServerStatus();
    }

}
