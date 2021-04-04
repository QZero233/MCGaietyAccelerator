package com.qzero.server.console.commands;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.tunnel.GlobalTunnelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TunnelCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    @CommandMethod(commandName = "open_tunnel",needServerSelected = false,parameterCount = 1)
    private String openTunnel(String[] commandParts, String commandLine, ServerCommandContext context) {
        String tunnelPort=commandParts[1];
        int port=Integer.parseInt(tunnelPort);

        try {
            GlobalTunnelManager.getInstance().openTunnel(port);
            return "Tunnel is opened on port "+port;
        } catch (IOException e) {
            log.error("Failed to open tunnel for port "+port);
            return e.getMessage();
        }
    }

    @CommandMethod(commandName = "close_tunnel",needServerSelected = false,parameterCount = 1)
    private String closeTunnel(String[] commandParts, String commandLine, ServerCommandContext context) {
        String tunnelPort=commandParts[1];
        int port=Integer.parseInt(tunnelPort);

        try {
            GlobalTunnelManager.getInstance().closeTunnel(port);
            return String.format("Tunnel with port %d is closed", port);
        } catch (IOException e) {
            log.error("Failed to close tunnel for port "+port);
            return e.getMessage();
        }
    }

}
