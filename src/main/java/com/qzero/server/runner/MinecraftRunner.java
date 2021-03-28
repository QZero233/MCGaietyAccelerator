package com.qzero.server.runner;

import com.qzero.server.config.MinecraftServerConfiguration;
import com.qzero.server.console.ConsoleMonitor;
import com.qzero.server.console.InGameCommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinecraftRunner {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Logger listenLogger = LoggerFactory.getLogger("mc_servers_listen_logger");

    public enum ServerStatus {
        STARTING,
        RUNNING,
        STOPPED
    }

    private MinecraftServerConfiguration configuration;

    private Map<String, ServerOutputListener> outputListenerMap = new HashMap<>();

    private ConsoleMonitor consoleMonitor;

    private ServerStatus serverStatus = ServerStatus.STOPPED;

    private InGameCommandListener commandListener;

    public MinecraftRunner(MinecraftServerConfiguration configuration) {
        this.configuration = configuration;
        commandListener = new InGameCommandListener(configuration.getServerName());
        registerOutputListener(commandListener);
    }

    public void registerOutputListener(ServerOutputListener listener) {
        synchronized (outputListenerMap) {
            outputListenerMap.put(listener.getListenerId(), listener);
        }
    }

    public void unregisterOutputListener(String listenerId) {
        synchronized (outputListenerMap) {
            outputListenerMap.remove(listenerId);
        }
    }

    public void sendCommand(String commandLine) {
        if (consoleMonitor == null || serverStatus != ServerStatus.RUNNING)
            throw new IllegalStateException(String.format("[MinecraftRunner]Server is not running, failed to execute command [%s] for server [%s]",
                    commandLine, configuration.getServerName()));
        consoleMonitor.executeCommand(commandLine);
    }

    public void stopServer() {
        consoleMonitor.executeCommand("/stop");
    }

    public void startServer() {
        serverStatus = ServerStatus.STARTING;
        broadcastEvent(ServerOutputListener.ServerEvent.SERVER_STARTING);

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
                            broadcastEvent(ServerOutputListener.ServerEvent.SERVER_STOPPED);
                            break;
                        }

                        log.info((String.format("[Server-%s]", configuration.getServerName()) + output));
                        listenLogger.info((String.format("[Server-%s]", configuration.getServerName()) + output));

                        broadcastOutput(output, ServerOutputListener.OutputType.TYPE_NORMAL);

                        if (output.matches(".*Done.*For help, type \"help\"")) {
                            //Which means server has started
                            serverStatus = ServerStatus.RUNNING;
                            broadcastEvent(ServerOutputListener.ServerEvent.SERVER_STARTED);
                        }

                    }
                } catch (Exception e) {
                    log.error("Failed to read normal output for server " + configuration.getServerName(), e);
                    listenLogger.error("Failed to read normal output for server " + configuration.getServerName(), e);
                    broadcastOutput(e.getMessage(), ServerOutputListener.OutputType.TYPE_ERROR);
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
                        broadcastOutput(output, ServerOutputListener.OutputType.TYPE_ERROR);

                    }
                } catch (Exception e) {
                    log.error("Failed to read error output for server " + configuration.getServerName(), e);
                    listenLogger.error("Failed to read error output for server " + configuration.getServerName(), e);
                    broadcastOutput(e.getMessage(), ServerOutputListener.OutputType.TYPE_ERROR);
                }

            }
        }.start();
    }

    public void forceStopServer() {
        serverStatus = ServerStatus.STOPPED;
        broadcastEvent(ServerOutputListener.ServerEvent.SERVER_STOPPED);
        try {
            consoleMonitor.forceStop();
        } catch (IOException e) {
            log.error(String.format("[Server-%s(ERROR)]Error when force stopping server", configuration.getServerName()), e);
        }
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    private void broadcastOutput(String output, ServerOutputListener.OutputType type) {
        synchronized (outputListenerMap) {
            Set<String> keySet = outputListenerMap.keySet();
            String serverName = configuration.getServerName();
            for (String key : keySet) {
                outputListenerMap.get(key).receivedOutputLine(serverName, output, type);
            }
        }
    }

    private void broadcastEvent(ServerOutputListener.ServerEvent event) {
        synchronized (outputListenerMap) {
            Set<String> keySet = outputListenerMap.keySet();
            String serverName = configuration.getServerName();
            for (String key : keySet) {
                outputListenerMap.get(key).receivedServerEvent(serverName, event);
            }
        }
    }

}
