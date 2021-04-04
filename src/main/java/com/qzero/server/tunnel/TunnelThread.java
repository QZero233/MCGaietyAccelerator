package com.qzero.server.tunnel;

import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TunnelThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private int port;
    private Socket host;
    private PrintWriter hostPw;

    private Map<String,ClientConnection> clientConnections=new HashMap<>();

    private boolean running=true;

    private ServerSocket serverSocket;

    public TunnelThread(int port) throws IOException {
        this.port = port;
        serverSocket=new ServerSocket(port);
    }

    @Override
    public void run() {
        super.run();

        try {
            while (running){
                Socket socket=serverSocket.accept();
                new ClientReceptionThread(socket,this).start();
            }
        }catch (Exception e){
            log.error("Failed to accept client, tunnel port is "+port,e);
        }
    }

    public void closeTunnel() throws IOException {
        serverSocket.close();
        host.close();

        Set<String> keySet=clientConnections.keySet();
        for(String key:keySet){
            clientConnections.get(key).stopContact();;
        }
    }

    public void setHost(Socket socket){
        host=socket;
        try {
            hostPw=new PrintWriter(host.getOutputStream());
        } catch (IOException e) {

        }

        hostPw.println("//You are host now");
        hostPw.flush();
    }

    public boolean isHostConnected(){
        return host!=null;
    }

    public void addClientConnection(Socket client,byte[] preSentBytes){
        String clientId=UUIDUtils.getRandomUUID();

        ClientConnection clientConnection=new ClientConnection(preSentBytes);
        clientConnection.setClient(client);

        clientConnections.put(clientId,clientConnection);
        hostPw.println("contact "+clientId);
        hostPw.flush();
    }

    public void contactClient(String clientId,Socket contactSocket){
        ClientConnection clientConnection=clientConnections.get(clientId);
        if(clientConnection==null || clientConnection.isContacted())
            throw new IllegalStateException("Can not contact this client");

        clientConnection.setHost(contactSocket);
        clientConnection.startContact();
    }

}
