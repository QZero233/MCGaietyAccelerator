package com.qzero.server;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.environment.ServerEnvironment;
import com.qzero.server.config.environment.ServerEnvironmentChecker;
import com.qzero.server.config.mcga.MCGAConfiguration;
import com.qzero.server.console.*;
import com.qzero.server.console.client.ClientModeConsole;
import com.qzero.server.console.remote.RemoteConsoleServerManager;
import com.qzero.server.plugin.GlobalPluginManager;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServerManagerMain {

    private static Logger log= LoggerFactory.getLogger(ServerManagerMain.class);

    public static void main(String[] args) {
        //Get from file
        Map<String,String> argsFromFile=null;
        try {
            argsFromFile=readLaunchArguments();
        }catch (Exception e){
            log.error("Failed to read arguments from file",e);
        }

        //Get from args (top priority)
        Map<String,String> argsFromArgs=convertArgs(args);

        Map<String,String> convertedArgs;
        if(argsFromFile==null || argsFromFile.isEmpty())
            //Just use args
            convertedArgs=argsFromArgs;
        else{
            //need to merge
            argsFromFile.putAll(argsFromArgs);//Since args has top priority
            convertedArgs=argsFromFile;
        }

        //Check if it's in local console mode
        if(convertedArgs.containsKey("local_console")){
            String terminalPath=convertedArgs.get("local_console");
            System.out.println("Local console mode,terminal path: "+terminalPath);
            new ConsoleMonitorThread(terminalPath).start();
            return;
        }

        //Check if it's client mode
        if(convertedArgs.containsKey("client_mode")){
            String portInString=convertedArgs.get("client_mode");
            if(!portInString.matches("[0-9]*")) {
                log.error("Failed to start client due to illegal port : " + portInString);
                return;
            }

            int port=Integer.parseInt(portInString);
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
            initializeServer();
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
        if(convertedArgs.containsKey("remote_console_port")){
            String portInString=convertedArgs.get("remote_console_port");
            if(!portInString.matches("[0-9]*")){
                log.error("Failed to start remote console due to illegal port : "+portInString);
            }else{
                //Start server
                int port=Integer.parseInt(portInString);

                try {
                    RemoteConsoleServerManager manager=RemoteConsoleServerManager.getInstance();
                    manager.startServer(port);
                    log.info("Remote console server started successfully on port "+port);
                }catch (Exception e){
                    log.error("Failed to start remote console server on port "+port,e);
                }

            }
        }

        //Start local console
        CommandThread thread=new CommandThread(System.in,System.out,"#localConsole",true);
        thread.start();



    }

    /**
     * Read arguments in file args.config
     * With format a=b \n c=d \n d
     * For d, the value will be an empty string
     * @return
     * @throws Exception
     */
    public static Map<String,String> readLaunchArguments() throws Exception{
        if(!new File("args.config").exists())
            return new HashMap<>();
        String content=StreamUtils.readFileInStringRemoveR(new File("args.config"));
        String[] lines=content.split("\n");

        Map<String,String> result=new HashMap<>();
        for(String line:lines){
            String[] parts=line.split("=");

            if(parts.length>1){
                //a=b
                result.put(parts[0],parts[1]);
            }else{
                //b
                result.put(parts[0],"");
            }
        }

        return result;
    }

    /**
     * Convert arguments like -consolePath=/bin/bash -root into a map
     * If it's like -root, the value will be an empty string
     * @param args
     * @return
     */
    public static Map<String,String> convertArgs(String[] args){
        Map<String,String> result=new HashMap<>();

        for(String line:args){
            //It's the form of -a=b
            if(line.startsWith("-") && line.matches(".*=.*")){
                String[] parts=line.split("=");

                result.put(parts[0].replaceAll("^-",""),parts[1]);
            }
            //It's the form of -b
            else if(line.startsWith("-")){
                result.put(line.replaceAll("^-",""),"");
            }
        }

        return result;
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
