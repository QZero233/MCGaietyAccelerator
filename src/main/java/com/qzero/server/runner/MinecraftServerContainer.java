package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftEnvironmentChecker;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import com.qzero.server.exception.MinecraftServerStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public void startServer(String serverName) throws IOException {
        MinecraftRunner runner;
        if(!serverRunnerMap.containsKey(serverName)){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new MinecraftServerNotFoundException(serverName,"start server");

            new MinecraftEnvironmentChecker(configuration).checkMinecraftServerEnvironment();

            runner=new MinecraftRunner(configuration);
            serverRunnerMap.put(serverName,runner);
        }else{
            runner=serverRunnerMap.get(serverName);
        }

        if(runner.getServerStatus()== MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"stopped","running","start server again");

        runner.startServer();
    }

    public void stopServer(String serverName){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null || runner.getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"running","stopped","stop server");

        runner.stopServer();
    }

    public void forceStopServer(String serverName){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null)
            throw new MinecraftServerStatusException(serverName,"running","stopped","forece stop server");
        runner.forceStopServer();
    }

    public void sendCommand(String serverName,String command){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null || runner.getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new MinecraftServerStatusException(serverName,"running","stopped","send command");

        runner.sendCommand(command);
    }

    public void registerOutputListener(String serverName,ServerOutputListener listener){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new MinecraftServerNotFoundException(serverName,"register output listener");

            runner=new MinecraftRunner(configuration);
            serverRunnerMap.put(serverName,runner);
        }

        runner.registerOutputListener(listener);
    }

    public void unregisterOutputListener(String serverName,String listenerId){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null)
            throw new MinecraftServerStatusException(serverName,"running","stopped","unregister listener");

        runner.unregisterOutputListener(listenerId);
    }

    public MinecraftRunner.ServerStatus getServerStatus(String serverName){
        MinecraftRunner runner=serverRunnerMap.get(serverName);
        if(runner==null)
            return MinecraftRunner.ServerStatus.STOPPED;

        return runner.getServerStatus();
    }

}
