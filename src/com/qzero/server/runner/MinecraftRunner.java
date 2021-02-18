package com.qzero.server.runner;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.console.CommandThread;
import com.qzero.server.console.ConsoleMonitor;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftRunner {

    public enum ServerStatus{
        STARTING,
        RUNNING,
        STOPPED
    }

    private MinecraftServerConfiguration configuration;

    private Map<String,ServerOutputListener> outputListenerMap=new HashMap<>();

    private ConsoleMonitor consoleMonitor;

    private ServerStatus serverStatus=ServerStatus.STOPPED;

    private Map<String, CommandThread> inGameCommandThreadMap=new HashMap<>();

    public MinecraftRunner(MinecraftServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void registerOutputListener(ServerOutputListener listener){
        synchronized (outputListenerMap){
            outputListenerMap.put(listener.getListenerId(),listener);
        }
    }

    public void unregisterOutputListener(String listenerId){
        synchronized (outputListenerMap){
            outputListenerMap.remove(listenerId);
        }
    }

    public void sendCommand(String commandLine){
        if(consoleMonitor==null || serverStatus!=ServerStatus.RUNNING)
            throw new IllegalStateException(String.format("[MinecraftRunner]Server is not running, failed to execute command [%s] for server [%s]",
                    commandLine,configuration.getServerName()));
        consoleMonitor.executeCommand(commandLine);
    }

    public void stopServer(){
        consoleMonitor.executeCommand("/stop");
    }

    public void startServer() {
        serverStatus=ServerStatus.STARTING;

        String javaPath=configuration.getJavaPath();
        String javaParameter=configuration.getJavaParameter();
        //javaParameter+=" -Duser.dir="+configuration.getServerName()+"/";
        javaParameter+= String.format(" -jar %s",
                configuration.getServerJarFileName());

        try {
            consoleMonitor=new ConsoleMonitor(javaPath,javaParameter,new File(configuration.getServerName()+"/"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[MinecraftRunner]Failed to initialize console monitor while starting server "+configuration.getServerName());
            return;
        }

        //Read normal output
        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    while (true){
                        String output=consoleMonitor.readNormalOutput();
                        if(output==null){
                            //Which means server stopped
                            System.out.println(String.format("[MinecraftRunner-%s]Server stopped", configuration.getServerName()));
                            serverStatus=ServerStatus.STOPPED;
                            break;
                        }
                        System.out.println(String.format("[MinecraftRunner-%s]", configuration.getServerName())+output);
                        broadcastOutput(output, ServerOutputListener.OutputType.TYPE_NORMAL);

                        if(output.matches(".*Done.*For help, type \"help\"")){
                            //Which means server has started
                            serverStatus=ServerStatus.RUNNING;
                        }else if(output.matches(".*<.*> cmd:.*")){
                            //Start in game command thread
                            Pattern pattern=Pattern.compile("<.*>");
                            Matcher matcher=pattern.matcher(output);

                            matcher.find();
                            String id=matcher.group();
                            id=id.replace("<","");
                            id=id.replace(">","");
                            if(!GlobalConfigurationManager.getInstance().checkInGameOP(id)){
                                tellToPlayerInGame(id,"You are not one of the in-game operators");
                            }else {
                                pattern=Pattern.compile(" cmd:.*");
                                matcher=pattern.matcher(output);
                                matcher.find();

                                String command=matcher.group();
                                command=command.replace(" cmd:","");

                                if(command.equals("on")){
                                    inGameCommandThreadMap.put(id,new CommandThread(null,null));
                                    tellToPlayerInGame(id,"Cmd mode is on now");
                                }else if(command.equals("off")){
                                    inGameCommandThreadMap.remove(id);
                                    tellToPlayerInGame(id,"Cmd mode is off now");
                                }else{
                                    if(!inGameCommandThreadMap.containsKey(id)){
                                        tellToPlayerInGame(id,"Please turn on cmd mode first");
                                    }else{
                                        String returnValue=inGameCommandThreadMap.get(id).executeInGameCommand(command);
                                        tellToPlayerInGame(id,returnValue);
                                    }
                                }


                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.err.println("[MinecraftRunner]Failed to read normal output for server "+configuration.getServerName());
                }

            }
        }.start();

        //Read error output
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    while (true){
                        String output=consoleMonitor.readErrorOutput();
                        if(output==null){
                            break;
                        }
                        System.out.println(String.format("[MinecraftRunner-%s(ERROR)]", configuration.getServerName())+output);
                        broadcastOutput(output, ServerOutputListener.OutputType.TYPE_ERROR);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.err.println("[MinecraftRunner]Failed to read error output for server "+configuration.getServerName());
                }

            }
        }.start();
    }

    public void forceStopServer(){
        serverStatus=ServerStatus.STOPPED;
        try {
            consoleMonitor.forceStop();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(String.format("[MinecraftRunner-%s(ERROR)]Error when force stopping server", configuration.getServerName()));
        }
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    private void broadcastOutput(String output, ServerOutputListener.OutputType type){
        synchronized (outputListenerMap){
            Set<String> keySet=outputListenerMap.keySet();
            String serverName=configuration.getServerName();
            for(String key:keySet){
                outputListenerMap.get(key).receivedOutputLine(serverName,output,type);
            }
        }
    }

    private void tellToPlayerInGame(String playerName,String message){
        if(serverStatus!=ServerStatus.RUNNING)
            throw new IllegalStateException("Server is not running");

        consoleMonitor.executeCommand(String.format("/tell %s %s", playerName,message));
    }

}
