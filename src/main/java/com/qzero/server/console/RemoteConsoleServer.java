package com.qzero.server.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteConsoleServer extends Thread {

    private ServerSocket serverSocket;

    private boolean running=true;

    public RemoteConsoleServer(int port) throws Exception {
        serverSocket=new ServerSocket(port);
    }

    public void stopServer() throws IOException {
        serverSocket.close();
        running=false;
    }

    @Override
    public void run() {
        super.run();

        try {

            while (running){
                Socket socket=serverSocket.accept();
                System.out.println(String.format("[RemoteConsoleServer]Client %s connected", socket.getInetAddress()+""));

                InputStream is=socket.getInputStream();
                OutputStream os=socket.getOutputStream();
                CommandThread thread=new CommandThread(is,os);
                thread.start();
            }

        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error when accepting client");
        }
    }
}
