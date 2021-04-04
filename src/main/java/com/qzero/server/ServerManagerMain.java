package com.qzero.server;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.ServerEnvironment;
import com.qzero.server.config.ServerEnvironmentChecker;
import com.qzero.server.console.CommandThread;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.runner.MinecraftServerContainerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerManagerMain {

    private static Logger log= LoggerFactory.getLogger(ServerManagerMain.class);

    public static void main(String[] args) throws IOException {
        try {
            initializeServer();
        } catch (Exception e) {
            log.error("Failed to initialize server",e);
            return;
        }

        CommandThread thread=new CommandThread(System.in,System.out);
        thread.start();
    }

    public static void initializeServer() throws IllegalAccessException, IOException, InstantiationException, ClassNotFoundException {
        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();

        configurationManager.loadConfig();
        log.info("Server configuration loaded");

        ServerEnvironment environment=configurationManager.getServerEnvironment();
        ServerEnvironmentChecker serverEnvironmentChecker=new ServerEnvironmentChecker(environment);
        serverEnvironmentChecker.checkEnvironment();
        log.info("Server environment checked");

        //TODO 使用配置文件来配置Container
        MinecraftServerContainerSession.getInstance().initContainer(MinecraftServerContainerSession.ContainerType.COMMON);
        log.info(String.format("Minecraft server container loaded(type: %s)", "Common"));

        ServerCommandExecutor commandExecutor=ServerCommandExecutor.getInstance();
        commandExecutor.loadCommands();
        log.info("Server commands loaded");
    }

}
