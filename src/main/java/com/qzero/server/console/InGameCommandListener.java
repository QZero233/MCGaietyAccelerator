package com.qzero.server.console;

import com.qzero.server.runner.MinecraftRunner;
import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InGameCommandListener implements ServerOutputListener {

    private String listenerId;

    private MinecraftServerContainer container;

    private ServerCommandExecutor executor;

    public InGameCommandListener() {
        listenerId = UUIDUtils.getRandomUUID();

        container = MinecraftServerContainerSession.getInstance().getCurrentContainer();
        executor = ServerCommandExecutor.getInstance();
    }

    @Override
    public String getListenerId() {
        return listenerId;
    }

    @Override
    public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
        if (outputType == OutputType.TYPE_ERROR)
            return;

        if (outputLine.matches(".*<.*> #.*")) {
            //Start in game command thread
            Pattern pattern = Pattern.compile("<.*>");
            Matcher matcher = pattern.matcher(outputLine);

            matcher.find();
            String id = matcher.group();
            id = id.replace("<", "");
            id = id.replace(">", "");

            pattern = Pattern.compile(" #.*");
            matcher = pattern.matcher(outputLine);
            matcher.find();

            String command = matcher.group();
            command = command.replace(" #", "");

            if(command.startsWith("reset_context")){
                GlobalOperatorContextContainer.getInstance().removeContext(id);
                tellToPlayerInGame(serverName,id,"Operator context reset");
                return;
            }

            ServerCommandContext context = GlobalOperatorContextContainer.getInstance().getContext(id);
            if (context == null) {
                context = new ServerCommandContext();
                context.setCurrentServer(serverName);
                context.setOperatorId(id);
                context.setEnvType(ServerCommandContext.ExecuteEnvType.IN_GAME);
                GlobalOperatorContextContainer.getInstance().saveContext(id,context);
            }

            String returnValue = executor.executeCommand(command, context);
            tellToPlayerInGame(serverName,id, returnValue);
        }
    }

    @Override
    public void receivedServerEvent(String serverName, ServerEvent event) {

    }

    private void tellToPlayerInGame(String serverName,String playerName, String message) {
        if(container==null)
            container=MinecraftServerContainerSession.getInstance().getCurrentContainer();

        if (container.getServerOperator(serverName)==null || container.getServerOperator(serverName).getServerStatus() != MinecraftRunner.ServerStatus.RUNNING)
            throw new IllegalStateException("Server is not running");

        String[] lines = message.split("\n");
        for (String line : lines) {
            container.getServerOperator(serverName).sendCommand(String.format("/tell %s %s", playerName, line));
        }
    }

}
