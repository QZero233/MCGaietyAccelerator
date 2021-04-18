package com.qzero.server;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.environment.ServerEnvironment;
import com.qzero.server.config.environment.ServerEnvironmentChecker;
import com.qzero.server.config.manager.ServerManagerConfiguration;
import com.qzero.server.console.CommandThread;
import com.qzero.server.console.RemoteConsoleServer;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.runner.MinecraftServerContainerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManagerMain {

    private static Logger log= LoggerFactory.getLogger(ServerManagerMain.class);

    public static void main(String[] args) {
        try {
            initializeServer();
        } catch (Exception e) {
            log.error("Failed to initialize server",e);
            return;
        }

        CommandThread thread=new CommandThread(System.in,System.out);
        thread.start();
    }

    public static void initializeServer() throws Exception {
        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();

        configurationManager.loadConfig();
        log.info("Server configuration loaded");

        ServerEnvironment environment=configurationManager.getEnvironmentConfigurationManager().getServerEnvironment();
        ServerEnvironmentChecker serverEnvironmentChecker=new ServerEnvironmentChecker(environment);
        serverEnvironmentChecker.checkEnvironment();
        log.info("Server environment checked");

        ServerManagerConfiguration managerConfiguration= configurationManager.getManagerConfigurationManager().getManagerConfiguration();
        MinecraftServerContainerSession.getInstance().initContainer();
        log.info(String.format("Minecraft server container loaded(type: %s)", managerConfiguration.getServerType()));

        if(managerConfiguration.isEnableRemoteConsole()
                && managerConfiguration.getContainerType()!= MinecraftServerContainerSession.ContainerType.SINGLE_PORT){
            //Start remote console
            new RemoteConsoleServer(managerConfiguration.getRemoteConsolePortInInt()).start();
            log.info("Remote console started on port "+managerConfiguration.getRemoteConsolePortInInt());
        }

        ServerCommandExecutor commandExecutor=ServerCommandExecutor.getInstance();
        commandExecutor.loadCommands();
        log.info("Server commands loaded");
    }

}
