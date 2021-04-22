package com.qzero.server.console;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.utils.SHA256Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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

                os.write("Please input your admin id:".getBytes());
                Scanner scanner=new Scanner(is);
                String id=scanner.nextLine();

                os.write("Please input your password:".getBytes());
                String password=scanner.nextLine();
                String passwordHash= SHA256Utils.getHexEncodedSHA256(password);

                if(!GlobalConfigurationManager.getInstance().getAuthorizeConfigurationManager().checkAdminInfo(id,passwordHash)){
                    os.write("Login failed, please check your id and password\n".getBytes());
                    socket.close();
                }else{
                    CommandThread thread=new CommandThread(is,os);
                    thread.start();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error when accepting client");
        }
    }
}
