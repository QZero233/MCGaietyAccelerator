package com.qzero.server.tunnel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GlobalTunnelManager {

    private Map<Integer,TunnelThread> tunnelMap=new HashMap<>();

    private static GlobalTunnelManager instance;

    public static GlobalTunnelManager getInstance() {
        if(instance==null)
            instance=new GlobalTunnelManager();
        return instance;
    }

    private GlobalTunnelManager(){

    }

    public void openTunnel(int port) throws IOException {
        TunnelThread tunnelThread=new TunnelThread(port);
        tunnelThread.start();
        tunnelMap.put(port,tunnelThread);
    }

    public void closeTunnel(int port) throws IOException {
        TunnelThread tunnelThread=tunnelMap.get(port);
        tunnelThread.closeTunnel();
    }

}
