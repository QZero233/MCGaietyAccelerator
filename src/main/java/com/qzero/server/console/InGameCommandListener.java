package com.qzero.server.console;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.runner.MinecraftRunner;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InGameCommandListener implements ServerOutputListener {

    private String listenerId;

    private String attachedServerName;

    private ServerCommandContext context;

    private MinecraftServerContainer container;

    private ServerCommandExecutor executor;

    public InGameCommandListener(String serverName) {
        attachedServerName=serverName;
        listenerId= UUIDUtils.getRandomUUID();
        context=new ServerCommandContext();

        context.setCurrentServer(serverName);

        container=MinecraftServerContainer.getInstance();
        executor=ServerCommandExecutor.getInstance();
    }

    @Override
    public String getListenerId() {
        return listenerId;
    }

    @Override
    public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
        if(outputType==OutputType.TYPE_ERROR)
            return;

        if(outputLine.matches(".*<.*> #.*")){
            //Start in game command thread
            Pattern pattern=Pattern.compile("<.*>");
            Matcher matcher=pattern.matcher(outputLine);

            matcher.find();
            String id=matcher.group();
            id=id.replace("<","");
            id=id.replace(">","");
            if(!GlobalConfigurationManager.getInstance().checkInGameOP(id)){
                tellToPlayerInGame(id,"You are not one of the in-game operators");
            }else {
                pattern=Pattern.compile(" #.*");
                matcher=pattern.matcher(outputLine);
                matcher.find();

                String command=matcher.group();
                command=command.replace(" #","");


                String returnValue=executor.executeCommand(command,context);
                tellToPlayerInGame(id,returnValue);
            }
        }
    }

    private void tellToPlayerInGame(String playerName,String message){
        if(container.getServerOperator(attachedServerName).getServerStatus()!= MinecraftRunner.ServerStatus.RUNNING)
            throw new IllegalStateException("Server is not running");

        String[] lines=message.split("\n");
        for(String line:lines){
            container.getServerOperator(attachedServerName).sendCommand(String.format("/tell %s %s", playerName,line));
        }
    }

}
