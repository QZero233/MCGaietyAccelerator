package com.qzero.server;

import com.qzero.server.config.StartConfig;
import com.qzero.server.console.CommandThread;
import com.qzero.server.console.ConsoleMonitorThread;
import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.console.client.ClientModeConsole;
import com.qzero.server.console.remote.RemoteConsoleServerManager;
import com.qzero.server.plugin.GlobalPluginManager;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.service.AdminAccountService;
import com.qzero.server.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class ServerManagerMain {

    private static Logger log= LoggerFactory.getLogger(ServerManagerMain.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServerManagerMain.class,args);

        StartConfig startConfig=SpringUtil.getBean(StartConfig.class);
        log.debug("Start config: "+startConfig);


        //Check if it's in local console mode
        if(startConfig.isLocalConsoleMode()){
            String terminalPath=startConfig.getLocalConsolePath();
            System.out.println("Local console mode,terminal path: "+terminalPath);
            new ConsoleMonitorThread(terminalPath).start();
            return;
        }

        //Check if it's client mode
        if(startConfig.isClientMode()){
            int port= startConfig.getConnectPort();

            try {
                ClientModeConsole clientModeConsole=new ClientModeConsole(port);
                clientModeConsole.start();
                log.info("Client started successfully");
            }catch (Exception e){
                log.error("Failed to start client",e);
            }

            return;
        }

        //Initialize server
        try {
            initializeServer(startConfig);
        } catch (Exception e) {
            log.error("Failed to initialize server",e);
            return;
        }

        //Execute auto exec commands
        try {
            log.info("Executing auto_exec commands");
            executeAutoExecCommands();
        }catch (Exception e){
            log.error("Failed to execute command",e);
            return;
        }

        //Check if it needs remote console
        if(startConfig.isEnableRemoteConsole()){
            int port= startConfig.getRemoteConsolePort();
            try {
                RemoteConsoleServerManager manager=RemoteConsoleServerManager.getInstance();
                manager.startServer(port);
                log.info("Remote console server started successfully on port "+port);
            }catch (Exception e){
                log.error("Failed to start remote console server on port "+port,e);
            }
        }

        //Start local console
        CommandThread thread=new CommandThread(System.in,System.out,"#localConsole",true);
        thread.start();
    }

    public static void initializeServer(StartConfig startConfig) throws Exception {
        log.info("Server configuration loaded");

        //Load plugins
        GlobalPluginManager globalPluginManager=GlobalPluginManager.getInstance();
        globalPluginManager.scanAndLoadAutoLoadPlugins();
        log.info("Finished scanning auto-load plugins");

        //Load container
        MinecraftServerContainerSession.getInstance().initCurrentContainer(startConfig.getContainerName());
        log.info(String.format("Minecraft server container loaded(name: %s)",
                startConfig.getContainerName()==null?"common":startConfig.getContainerName()));

        ServerCommandExecutor commandExecutor=ServerCommandExecutor.getInstance();
        commandExecutor.loadCommands();
        log.info("Server commands loaded");
    }

    public static void executeAutoExecCommands() throws Exception{
        File file=new File("auto_exec.txt");
        if(!file.exists())
            return;

        String[] commandLines=StreamUtils.readFileInStringRemoveR(file).split("\n");

        ServerCommandExecutor commandExecutor=ServerCommandExecutor.getInstance();
        ServerCommandContext context=new ServerCommandContext();
        for(String command:commandLines){
            commandExecutor.executeCommand(command,context);
            log.debug("Executed command "+command);
        }

        log.info("Execute auto_exec successfully");
    }

}
