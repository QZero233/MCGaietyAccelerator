package com.qzero.server.tunnel;

import java.io.IOException;
import java.net.Socket;

public class ClientConnection {

    private RouteThread hostToClient;
    private RouteThread clientToHost;

    private byte[] preSentBytes;

    private Socket host;
    private Socket client;

    private boolean contacted=false;

    private ClientDisconnectedListener listener;

    public ClientConnection(byte[] preSentBytes, ClientDisconnectedListener listener) {
        this.preSentBytes = preSentBytes;
        this.listener = listener;
    }

    public void setHost(Socket host) {
        this.host = host;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public boolean isContacted() {
        return contacted;
    }

    public void stopContact() throws IOException {
        host.close();
        client.close();
    }

    public void contactDisconnected(){
        try {
            host.close();
        }catch (Exception e){

        }

        try {
            client.close();
        }catch (Exception e){

        }
        listener.onDisconnected();
    }

    public void startContact(){
        if(host==null || client==null)
            throw new IllegalStateException("Either host or client is not ready");

        ClientDisconnectedListener listener=(()->{contactDisconnected();});

        hostToClient=new RouteThread(null,host,client,listener);
        clientToHost=new RouteThread(preSentBytes,client,host,listener);

        hostToClient.start();
        clientToHost.start();

        contacted=true;
    }

}
