package com.qzero.server.console;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.runner.MinecraftRunner;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This thread can receive commands to manage the program and output the result
 */
public class CommandThread extends Thread {

    private InputStream is;
    private OutputStream os;

    private PrintWriter printWriter;

    private boolean running=true;

    private String currentServer=null;

    private MinecraftServerContainer container;

    private String listenerId=null;

    public CommandThread(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        container=MinecraftServerContainer.getInstance();
    }

    @Override
    public void run() {
        super.run();
        try {
            printWriter=new PrintWriter(os);
            Scanner scanner=new Scanner(is);

            printWriter.print("Command>");
            printWriter.flush();

            while (running){
                String commandLine=scanner.nextLine();
                if(commandLine==null){
                    break;
                }

                String[] parts=commandLine.split(" ");
                String returnValue=executeCommand(parts[0],parts,commandLine);

                printWriter.println("Executing "+commandLine);
                printWriter.println("-------Begin Return------------");
                printWriter.println(returnValue);
                printWriter.println("-------End Return------------");
                printWriter.println("");
                printWriter.print("Command>");
                printWriter.flush();
            }
        }catch (Exception e){

        }

    }

    public String executeInGameCommand(String commandLine){
        if(commandLine.equals("listen_output") || commandLine.equals("cancel_output")){
            return "Command is not supported";
        }

        String[] parts=commandLine.split(" ");
        return executeCommand(parts[0],parts,commandLine);
    }

    private String executeCommand(String commandBody, String[] commandParts,String fullCommandLine){
        commandBody=commandBody.toLowerCase();
        if(commandBody.equals("exit")){
            running=false;
            return "Command thread stopped, good bye";
        }else if(commandBody.equals("select")){//1 ServerName
            if(commandParts.length<2)
                return "Parameter count error, expected to be more than 1";

            String serverName=commandParts[1];
            if(GlobalConfigurationManager.getInstance().getMinecraftServerConfig(serverName)==null)
                return String.format("Failed to select, server named %s does not exist", serverName);

            if(currentServer!=null){
                //Unregister listener when switch server
                if(listenerId!=null){
                    container.unregisterOutputListener(currentServer,listenerId);
                    listenerId=null;
                }

            }

            currentServer=serverName;
            return "Selected server "+serverName;
        }else if(commandBody.equals("current_server")){
            if(currentServer==null)
                return "No server selected";
            return currentServer;
        }else if(commandBody.equals("show_all_servers")){
            Map<String, MinecraftServerConfiguration> configurationMap=GlobalConfigurationManager.getInstance().getMcServers();
            if(configurationMap.isEmpty())
                return "There has been no server yet now";

            Set<String> nameSet=configurationMap.keySet();
            StringBuffer stringBuffer=new StringBuffer();
            for(String name:nameSet){
                stringBuffer.append(name);
                stringBuffer.append("\n");
            }

            return stringBuffer.toString();
        }else if(commandBody.equals("execute")){//Following is command line
            if(currentServer==null)
                return "No server is selected";

            String minecraftCommand=fullCommandLine.replace(commandBody+" ","");
            container.sendCommand(currentServer,minecraftCommand);
            return "Command has been sent";
        }else if(commandBody.equals("listen_output")){
            if(currentServer==null)
                return "No server is selected";

            if(listenerId!=null)
                return "Listening mode is on";

            listenerId= UUIDUtils.getRandomUUID();
            container.registerOutputListener(currentServer, new ServerOutputListener() {
                @Override
                public String getListenerId() {
                    return listenerId;
                }

                @Override
                public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
                    if(outputType==OutputType.TYPE_NORMAL)
                        printWriter.println(String.format("[%s]%s", serverName,outputLine));
                    else
                        printWriter.println(String.format("[ERROR][%s]%s", serverName,outputLine));

                    printWriter.flush();
                }
            });

            return "Registered output listener";
        }else if(commandBody.equals("cancel_output")){
            if(currentServer==null)
                return "No server is selected";

            if(listenerId==null)
                return "Listening mode is off, can not unregister listener";

            container.unregisterOutputListener(currentServer,listenerId);
            listenerId=null;

            return "Unregistered output listener";
        }else if(commandBody.equals("start")){
            if(currentServer==null)
                return "No server is selected";

            try {
                container.startServer(currentServer);
                return "Server started";
            }catch (Exception e){
                return e.getMessage();
            }
        }else if(commandBody.equals("stop")){
            if(currentServer==null)
                return "No server is selected";

            try {
                container.stopServer(currentServer);
                return "Server stopped";
            }catch (Exception e){
                return e.getMessage();
            }
        }else if(commandBody.equals("force_stop")){
            if(currentServer==null)
                return "No server is selected";

            try {
                container.forceStopServer(currentServer);
                return "Server force stopped";
            }catch (Exception e){
                return e.getMessage();
            }
        }else if(commandBody.equals("server_status")){
            if(currentServer==null)
                return "No server is selected";

            try {
                MinecraftRunner.ServerStatus serverStatus=container.getServerStatus(currentServer);
                switch (serverStatus){
                    case RUNNING:
                        return "Running";
                    case STARTING:
                        return "Starting";
                    case STOPPED:
                        return "Stopped";
                    default:
                        return "Unknown";
                }
            }catch (Exception e){
                return e.getMessage();
            }
        }else{
            return "Unknown command called "+commandBody;
        }

    }

}
