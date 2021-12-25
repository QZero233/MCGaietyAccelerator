package com.qzero.server.console.remote;

import java.net.ServerSocket;

public class RemoteConsoleServerManager {

    private static RemoteConsoleServerManager instance;

    private ServerSocket serverSocket;
    private RemoteConsoleServerThread serverThread;
    private boolean running=false;

    private RemoteConsoleServerManager(){

    }

    public static RemoteConsoleServerManager getInstance(){
        if(instance==null)
            instance=new RemoteConsoleServerManager();
        return instance;
    }

    public void startServer(int port) throws Exception{
        if(running)
            throw new Exception("Can not start remote console server when server is running");
        serverSocket=new ServerSocket(port);

        serverThread=new RemoteConsoleServerThread(serverSocket);
        serverThread.start();
        running=true;
    }

}
