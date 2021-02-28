package com.qzero.server.console;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteConsoleServer extends Thread {

    public static final int PORT=10193;

    private ServerSocket serverSocket;

    public RemoteConsoleServer() throws Exception {
        //serverSocket=new ServerSocket(PORT);
    }

    @Override
    public void run() {
        super.run();

        /*try {
            Socket socket=serverSocket.accept();
            System.out.println(String.format("[RemoteConsoleServer]Client %s connected", socket.getInetAddress()+""));

            InputStream is=socket.getInputStream();
            OutputStream os=socket.getOutputStream();
            CommandThread thread=new CommandThread(is,os);
            thread.start();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error when accepting client");
        }*/

        //TODO REMOTE MANAGE DO IT LATER
    }
}
