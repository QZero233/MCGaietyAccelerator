package com.qzero.server.console.commands;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.runner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerManageCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    private MinecraftServerContainer container;

    public ServerManageCommands() {
        container = MinecraftServerContainerSession.getInstance().getCurrentContainer();
    }

    @CommandMethod(commandName = "execute")
    private String execute(String[] commandParts, String commandLine, ServerCommandContext context) {
        String minecraftCommand = commandLine.replace("execute ", "");
        container.getServerOperator(context.getCurrentServer()).sendCommand(minecraftCommand);
        return "Command has been sent";
    }

    @CommandMethod(commandName = "start")
    private String start(String[] commandParts, String commandLine, ServerCommandContext context) {
        MinecraftServerOperator operator = container.getServerOperator(context.getCurrentServer());
        if (!operator.checkServerEnvironment())
            return "Server environment check failed, please use auto_config to config or do it manually";

        try {
            operator.startServer();
            return "Server started";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @CommandMethod(commandName = "stop")
    private String stop(String[] commandParts, String commandLine, ServerCommandContext context) {
        try {
            container.getServerOperator(context.getCurrentServer()).stopServer();
            return "Server stopped";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @CommandMethod(commandName = "force_stop")
    private String forceStop(String[] commandParts, String commandLine, ServerCommandContext context) {
        try {
            container.getServerOperator(context.getCurrentServer()).forceStopServer();
            return "Server force stopped";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @CommandMethod(commandName = "restart")
    private String restart(String[] commandParts, String commandLine, ServerCommandContext context) {
        MinecraftServerOperator operator=container.getServerOperator(context.getCurrentServer());
        operator.registerOutputListener(new ServerOutputListener() {

            private boolean used=false;

            @Override
            public String getListenerId() {
                return "listenerForRestartingServer";
            }

            @Override
            public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {

            }

            @Override
            public void receivedServerEvent(String serverName, ServerEvent event) {
                if(event==ServerEvent.SERVER_STOPPED && !used){
                    try {
                        operator.startServer();
                        used=true;
                    } catch (IOException e) {
                        log.error("Failed to restart server "+serverName,e);
                    }
                }
            }
        });
        operator.stopServer();
        return "Restarting server";
    }

    @CommandMethod(commandName = "server_status")
    private String serverStatus(String[] commandParts, String commandLine, ServerCommandContext context) {
        try {
            MinecraftRunner.ServerStatus serverStatus = container.getServerOperator(context.getCurrentServer()).
                    getServerStatus();
            switch (serverStatus) {
                case RUNNING:
                    return "Running";
                case STARTING:
                    return "Starting";
                case STOPPED:
                    return "Stopped";
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @CommandMethod(commandName = "switch_and_run", parameterCount = 1, needServerSelected = false)
    private String switchAndRun(String[] commandParts, String commandLine, ServerCommandContext context) {
        String serverName = commandParts[1];
        context.setCurrentServer(serverName);
        return start(commandParts, commandLine, context);
    }

    @CommandMethod(commandName = "stop_and_switch_and_run", parameterCount = 1)
    private String stopAndSwitchAndRun(String[] commandParts, String commandLine, ServerCommandContext context) {
        String serverName = commandParts[1];

        try {
            container.getServerOperator(context.getCurrentServer()).stopServer();
        } catch (Exception e) {
            return "Failed to stop current server";
        }

        context.setCurrentServer(serverName);
        return start(commandParts, commandLine, context);
    }

}
