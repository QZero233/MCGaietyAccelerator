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

    public ClientConnection(byte[] preSentBytes) {
        this.preSentBytes = preSentBytes;
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

    public void startContact(){
        if(host==null || client==null)
            throw new IllegalStateException("Either host or client is not ready");

        hostToClient=new RouteThread(null,host,client);
        clientToHost=new RouteThread(preSentBytes,client,host);

        hostToClient.start();
        clientToHost.start();

        contacted=true;
    }

}
