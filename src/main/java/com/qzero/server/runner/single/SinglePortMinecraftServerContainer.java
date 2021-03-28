package com.qzero.server.runner.single;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.config.MinecraftServerConfigurator;
import com.qzero.server.console.RemoteConsoleServer;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SinglePortMinecraftServerContainer implements MinecraftServerContainer {

    private int singlePort;
    private RemoteConsoleServer remoteConsoleServer;

    private Logger log= LoggerFactory.getLogger(getClass());

    private Map<String, MinecraftServerOperator> serverOperatorMap=new HashMap<>();

    public enum PortStatus{
        REMOTE_CONSOLE_OCCUPIED,
        MC_SERVER_OCCUPIED,
        FREE
    }

    private PortStatus portStatus;

    public SinglePortMinecraftServerContainer(int singlePort){
        this.singlePort=singlePort;
        try {
            startRemoteConsoleServer();
            portStatus=PortStatus.REMOTE_CONSOLE_OCCUPIED;
        } catch (Exception e) {
            log.error("Failed to start remote console server with singlePort "+singlePort,e);
        }
    }

    private void startRemoteConsoleServer() throws Exception {
        remoteConsoleServer=new RemoteConsoleServer(singlePort);
        remoteConsoleServer.start();
    }

    private void stopRemoteConsoleServer() throws IOException {
        if(remoteConsoleServer!=null){
            remoteConsoleServer.stopServer();
            remoteConsoleServer=null;
        }
    }

    @Override
    public MinecraftServerOperator getServerOperator(String serverName){
        MinecraftServerOperator operator;
        if(!serverOperatorMap.containsKey(serverName)){
            MinecraftServerConfiguration configuration= GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName);

            if(configuration==null)
                throw new MinecraftServerNotFoundException(serverName,"get server operator");

            //Change its port to specified port
            configuration.getCustomizedServerProperties().put("server-port",String.valueOf(singlePort));
            try {
                MinecraftServerConfigurator.configServer(configuration.getServerName());
            } catch (IOException e) {
                log.error(String.format("Failed to change the port of server %s in server.properties",
                        configuration.getServerName()),e);
                throw new IllegalStateException(String.format("Failed to change the port of server %s in server.properties",
                        configuration.getServerName()));
            }

            operator=new SinglePortMinecraftServerOperator(this,configuration);
            serverOperatorMap.put(serverName,operator);
        }else{
            operator=serverOperatorMap.get(serverName);
        }

        return operator;
    }

    /**
     *
     * @return ture if it's ok to start, otherwise not
     */
    public synchronized boolean requestToStartMinecraftServer(){
        if(portStatus==PortStatus.MC_SERVER_OCCUPIED)
            return false;

        if(portStatus==PortStatus.REMOTE_CONSOLE_OCCUPIED){
            //Stop remote console
            try {
                stopRemoteConsoleServer();
                portStatus=PortStatus.MC_SERVER_OCCUPIED;
                return true;
            } catch (IOException e) {
                log.error("Failed to stop remote console server",e);
                return false;
            }
        }
        return true;
    }

    public synchronized void markMinecraftServerStopped(){
        try {
            startRemoteConsoleServer();
            portStatus=PortStatus.REMOTE_CONSOLE_OCCUPIED;
        } catch (Exception e) {
            log.error("Failed to start remote console server with singlePort "+singlePort,e);
        }
    }

}
