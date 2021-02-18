package com.qzero.server;

import com.qzero.server.config.*;
import com.qzero.server.console.CommandThread;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ServerManagerMain {

    public static void main(String[] args) {
        try {
            initializeServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ServerManagerMain]Failed to initialize server");
            return;
        }

        /*try {
            RemoteConsoleServer consoleServer=new RemoteConsoleServer();
            consoleServer.start();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("[ServerManagerMain]Failed to start remote console server");
        }*/

        CommandThread thread=new CommandThread(System.in,System.out);
        thread.start();
    }

    public static void initializeServer() throws IllegalAccessException, IOException, InstantiationException {
        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();

        configurationManager.loadConfig();
        System.out.println("[ServerManagerMain]Server configuration loaded");

        ServerEnvironment environment=configurationManager.getServerEnvironment();
        ServerEnvironmentChecker serverEnvironmentChecker=new ServerEnvironmentChecker(environment);
        serverEnvironmentChecker.checkEnvironment();
        System.out.println("[ServerManagerMain]Server environment checked");

        Map<String, MinecraftServerConfiguration> mcServers=configurationManager.getMcServers();
        Set<String> serverNameSet=mcServers.keySet();
        for(String serverName:serverNameSet){
            MinecraftServerConfiguration configuration=mcServers.get(serverName);

            if(configuration.isNeedConfig()){
                MinecraftServerConfigurator.configServer(serverName);

                //Erase needConfig mark
                configurationManager.updateMinecraftServerConfig(serverName,"needConfig","false");
            }

            MinecraftEnvironmentChecker checker=new MinecraftEnvironmentChecker(configuration);
            checker.checkMinecraftServerEnvironment();
            System.out.println("[ServerManagerMain]Check Minecraft server environment for "+configuration.getServerName());
        }
        System.out.println("[ServerManagerMain]Minecraft servers checked");
    }

}
