package com.qzero.server;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.ServerEnvironment;
import com.qzero.server.config.ServerEnvironmentChecker;
import com.qzero.server.console.CommandThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerManagerMain {

    private static Logger log= LoggerFactory.getLogger(ServerManagerMain.class);

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
    }

}
