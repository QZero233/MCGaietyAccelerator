package com.qzero.server.console.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class ClientModeConsole{

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket socket;

    private ClientModeRelayThread localToRemote,remoteToLocal;

    public ClientModeConsole(int port) throws Exception{
        socket=new Socket("127.0.0.1",port);
    }

    public void start(){
        ClientModeRelayThread.OnRelayStopCallback callback=()->{
            //Close socket
            if(localToRemote!=null)
                localToRemote.stopRelay();

            if(remoteToLocal!=null)
                remoteToLocal.stopRelay();

            try {socket.close();}catch (Exception e1){}

            //Just exit the program because we can not stop relay thread read System.in
            System.exit(0);
        };


        try {
            localToRemote=new ClientModeRelayThread(System.in,socket.getOutputStream(),callback);
            remoteToLocal=new ClientModeRelayThread(socket.getInputStream(),System.out,callback);

            log.info("Relay session deploy successfully");

            localToRemote.start();
            remoteToLocal.start();
        }catch (Exception e){
            log.error("Failed to handle remote console messages, disconnect with remote console",e);

            try {socket.close();}catch (Exception e1){}
        }
    }

}
