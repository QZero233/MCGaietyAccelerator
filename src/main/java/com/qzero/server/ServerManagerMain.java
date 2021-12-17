package com.qzero.server;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.environment.ServerEnvironment;
import com.qzero.server.config.environment.ServerEnvironmentChecker;
import com.qzero.server.config.mcga.MCGAConfiguration;
import com.qzero.server.console.CommandThread;
import com.qzero.server.console.ConsoleMonitorThread;
import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.plugin.GlobalPluginManager;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ServerManagerMain {

    private static Logger log= LoggerFactory.getLogger(ServerManagerMain.class);

    public static void main(String[] args) {

        if(new File("localConsoleMode").exists()){
            String terminalPath;
            try {
                byte[] buf= StreamUtils.readFile(new File("localConsoleMode"));
                terminalPath=new String(buf);
            } catch (Exception e) {
                log.error("Failed to read local terminal path",e);
                return;
            }
            System.out.println("Local console mode,terminal path: "+terminalPath);
            new ConsoleMonitorThread(terminalPath).start();
            return;
        }

        if(args.length>=2 && args[0].equals("-local_console")){
            String terminalPath=args[1];
            System.out.println("Local console mode,terminal path: "+terminalPath);
            new ConsoleMonitorThread(terminalPath).start();
            return;
        }

        try {
            initializeServer();
        } catch (Exception e) {
            log.error("Failed to initialize server",e);
            return;
        }

        try {
            log.info("Executing auto_exec commands");
            executeAutoExecCommands();
        }catch (Exception e){
            log.error("Failed to execute command",e);
            return;

        }

        CommandThread thread=new CommandThread(System.in,System.out,"#localConsole",true);
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

        GlobalPluginManager globalPluginManager=GlobalPluginManager.getInstance();
        globalPluginManager.scanAndLoadAutoLoadPlugins();
        log.info("Finished scanning auto-load plugins");

        MCGAConfiguration managerConfiguration= configurationManager.getMcgaConfigurationManager().getMcgaConfiguration();
        MinecraftServerContainerSession.getInstance().initContainer();
        log.info(String.format("Minecraft server container loaded(name: %s)",
                managerConfiguration.getContainerName()==null?"common":managerConfiguration.getContainerName()));

        ServerCommandExecutor commandExecutor=ServerCommandExecutor.getInstance();
        commandExecutor.loadCommands();
        log.info("Server commands loaded");
    }

    public static void executeAutoExecCommands() throws Exception{
        File file=new File("auto_exec.txt");
        if(!file.exists())
            return;

        byte[] buf=StreamUtils.readFile(file);
        String[] commandLines=new String(buf).split("\n");

        ServerCommandExecutor commandExecutor=ServerCommandExecutor.getInstance();
        ServerCommandContext context=new ServerCommandContext();
        for(String command:commandLines){
            commandExecutor.executeCommand(command,context);
            log.debug("Executed command "+command);
        }

        log.info("Execute auto_exec successfully");
    }

}
