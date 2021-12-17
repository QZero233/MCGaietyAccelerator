package com.qzero.server.runner;

import com.qzero.server.config.minecraft.MinecraftServerConfiguration;
import com.qzero.server.console.ConsoleMonitor;
import com.qzero.server.console.InGameCommandContextSwitchListener;
import com.qzero.server.console.InGameCommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftRunner {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Logger listenLogger = LoggerFactory.getLogger("mc_servers_listen_logger");

    public enum ServerStatus {
        STARTING,
        RUNNING,
        STOPPED
    }

    private ConsoleMonitor consoleMonitor;

    private ServerStatus serverStatus = ServerStatus.STOPPED;

    private MinecraftServerOutputProcessCenter processCenter = MinecraftServerOutputProcessCenter.getInstance();

    private String serverName;

    public MinecraftRunner(String serverName) {
        this.serverName=serverName;
    }

    public void sendCommand(String commandLine) {
        if (consoleMonitor == null || serverStatus != ServerStatus.RUNNING)
            throw new IllegalStateException(String.format("[MinecraftRunner]Server is not running, failed to execute command [%s] for server [%s]",
                    commandLine, serverName));
        consoleMonitor.executeCommand(commandLine);
    }

    public void stopServer() {
        consoleMonitor.executeCommand("/stop");
    }

    //When start server, we should use the newest configuration
    public void startServer(MinecraftServerConfiguration configuration) {
        serverStatus = ServerStatus.STARTING;
        processCenter.broadcastServerEvent(serverName, ServerOutputListener.ServerEvent.SERVER_STARTING);

        String javaPath = configuration.getJavaPath();
        String javaParameter = configuration.getJavaParameter();
        javaParameter += String.format(" -jar %s",
                configuration.getServerJarFileName());

        try {
            consoleMonitor = new ConsoleMonitor(javaPath, javaParameter, new File(configuration.getServerName() + "/"));
        } catch (IOException e) {
            log.error("Failed to initialize console monitor while starting server " + configuration.getServerName(), e);
            return;
        }

        //Read normal output
        new Thread() {

            @Override
            public void run() {
                super.run();


                try {
                    while (true) {
                        String output = consoleMonitor.readNormalOutput();
                        if (output == null) {
                            //Which means server stopped
                            log.info(String.format("Server %s stopped", configuration.getServerName()));
                            serverStatus = ServerStatus.STOPPED;
                            processCenter.broadcastServerEvent(serverName, ServerOutputListener.ServerEvent.SERVER_STOPPED);
                            break;
                        }
                        processNormalOutput(output);
                    }
                } catch (Exception e) {
                    log.error("Failed to read normal output for server " + configuration.getServerName(), e);
                    listenLogger.error("Failed to read normal output for server " + configuration.getServerName(), e);
                    processCenter.broadcastOutput(serverName, e.getMessage(), ServerOutputListener.OutputType.TYPE_ERROR);
                }


            }
        }.start();

        //Read error output
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    while (true) {
                        String output = consoleMonitor.readErrorOutput();
                        if (output == null) {
                            break;
                        }
                        log.error(String.format("[Server-%s(ERROR)]", configuration.getServerName()) + output);
                        listenLogger.error(String.format("[Server-%s(ERROR)]", configuration.getServerName()) + output);
                        processCenter.broadcastOutput(serverName, output, ServerOutputListener.OutputType.TYPE_ERROR);
                    }
                } catch (Exception e) {
                    log.error("Failed to read error output for server " + configuration.getServerName(), e);
                    listenLogger.error("Failed to read error output for server " + configuration.getServerName(), e);
                    processCenter.broadcastOutput(serverName, e.getMessage(), ServerOutputListener.OutputType.TYPE_ERROR);
                }

            }
        }.start();
    }

    private void processNormalOutput(String output) {
        log.info((String.format("[Server-%s]", serverName) + output));
        listenLogger.info((String.format("[Server-%s]", serverName) + output));

        processCenter.broadcastOutput(serverName, output, ServerOutputListener.OutputType.TYPE_NORMAL);

        if (output.matches(".*Done.*For help, type \"help\"")) {
            //Which means server has started
            serverStatus = ServerStatus.RUNNING;
            processCenter.broadcastServerEvent(serverName, ServerOutputListener.ServerEvent.SERVER_STARTED);
        } else if (output.matches(".*: .* joined the game")) {
            Pattern pattern = Pattern.compile("(?<=: ).*(?= joined the game)");
            Matcher matcher = pattern.matcher(output);

            if (matcher.find()) {
                String playerName = matcher.group();
                processCenter.broadcastPlayerEvent(serverName, playerName, ServerOutputListener.PlayerEvent.JOIN);
            }
        } else if (output.matches(".*: .* left the game")) {
            Pattern pattern = Pattern.compile("(?<=: ).*(?= left the game)");
            Matcher matcher = pattern.matcher(output);

            if (matcher.find()) {
                String playerName = matcher.group();
                processCenter.broadcastPlayerEvent(serverName, playerName, ServerOutputListener.PlayerEvent.LEAVE);
            }
        }
    }

    public void forceStopServer() {
        serverStatus = ServerStatus.STOPPED;
        processCenter.broadcastServerEvent(serverName, ServerOutputListener.ServerEvent.SERVER_STOPPED);
        try {
            consoleMonitor.forceStop();
        } catch (IOException e) {
            log.error(String.format("[Server-%s(ERROR)]Error when force stopping server", serverName), e);
        }
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

}
