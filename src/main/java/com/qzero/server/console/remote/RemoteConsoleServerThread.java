package com.qzero.server.console.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;

public class RemoteConsoleServerThread extends Thread {

    private ServerSocket serverSocket;

    private Logger log= LoggerFactory.getLogger(getClass());

    public RemoteConsoleServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        super.run();

        try {
            while (true){
                Socket socket=serverSocket.accept();
                log.trace("Remote server accept client with ip "+socket.getInetAddress().getHostAddress());

                new RemoteConsoleProcessThread(socket).start();
            }
        }catch (Exception e){
            log.error("Remote console server failed to accept client, and it won't accept clients any more",e);
        }


    }
}
