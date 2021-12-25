package com.qzero.server.console.remote;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.authorize.AuthorizeConfigurationManager;
import com.qzero.server.console.CommandThread;
import com.qzero.server.utils.SHA256Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteConsoleProcessThread extends Thread{

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket socket;

    public RemoteConsoleProcessThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        OutputStream outputStream;
        InputStream inputStream;
        String id;
        //Prompt login
        try {
            outputStream=socket.getOutputStream();
            inputStream=socket.getInputStream();

            //If connection is in local network, need not login
            if(socket.getInetAddress().getHostAddress().equals("127.0.0.1")){
                id="#localConsole";
            } else{
                outputStream.write("Please input <MinecraftID> <password> to login:".getBytes(StandardCharsets.UTF_8));

                BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
                String line=br.readLine();
                String[] parts=line.split(" ");

                if(parts.length<2){
                    outputStream.write(("Illegal login line format : "+line).getBytes(StandardCharsets.UTF_8));
                    throw new IllegalArgumentException("Illegal login line format : "+line);
                }

                id=parts[0];
                String password=parts[1];

                AuthorizeConfigurationManager manager= GlobalConfigurationManager.getInstance().getAuthorizeConfigurationManager();
                if(!manager.checkAdminInfo(id,SHA256Utils.getHexEncodedSHA256(password))){
                    outputStream.write("Login failed, incorrect id and password".getBytes(StandardCharsets.UTF_8));
                    throw new IllegalArgumentException("Login failed, incorrect id and password");
                }else{
                    outputStream.write("Login successfully".getBytes(StandardCharsets.UTF_8));
                }
            }
        }catch (IllegalArgumentException e){
            //Just close connection without log
            try {socket.close();}catch (Exception e1){}
            return;
        }catch (Exception e){
            log.error("Remote console client can not login, disconnect with it",e);

            try {socket.close();}catch (Exception e1){}
            return;
        }

        //Start console
        CommandThread commandThread=new CommandThread(inputStream,outputStream,id,false);
        commandThread.setCallback(()->{
            //When console exit, close connection
            try {socket.close();}catch (Exception e1){}
        });
        commandThread.start();
    }
}
