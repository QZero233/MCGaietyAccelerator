package com.qzero.server.console;

import com.qzero.server.console.log.GameLogListener;
import com.qzero.server.console.log.GameLogOutputAppender;
import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This thread can receive commands to manage the program and output the result
 */
public class CommandThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private InputStream is;
    private OutputStream os;

    private PrintWriter printWriter;

    private boolean running=true;

    private ServerCommandContext context=new ServerCommandContext();

    private ServerCommandExecutor commandExecutor;

    private GameLogListener gameLogListener=new GameLogListener() {

        private String listenerId=UUIDUtils.getRandomUUID();

        @Override
        public String getListenerId() {
            return listenerId;
        }

        @Override
        public void log(String log) {
            try {
                os.write((log+"\n").getBytes());
            } catch (IOException e) {

            }
        }
    };

    public CommandThread(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        commandExecutor=ServerCommandExecutor.getInstance();
    }

    @Override
    public void run() {
        super.run();
        try {
            printWriter=new PrintWriter(os);
            Scanner scanner=new Scanner(is);

            printWriter.print("Command>");
            printWriter.flush();

            while (running){
                String commandLine=scanner.nextLine();
                if(commandLine==null){
                    break;
                }


                if(commandLine.toLowerCase().equals("exit")){
                    running=false;
                    printWriter.println("Command thread stopped, good bye");
                    break;
                }else if(commandLine.toLowerCase().equals("listen on")){
                    GameLogOutputAppender.registerLogListener(gameLogListener);

                    printWriter.println("Listen mode is on");
                    printWriter.println("");
                    printWriter.print("Command>");
                    printWriter.flush();
                    continue;
                }else if(commandLine.toLowerCase().equals("listen off")){
                    GameLogOutputAppender.unregisterLogListener(gameLogListener.getListenerId());

                    printWriter.println("Listen mode is off");
                    printWriter.println("");
                    printWriter.print("Command>");
                    printWriter.flush();
                    continue;
                }


                String returnValue=commandExecutor.executeCommand(commandLine,context);

                printWriter.println("Executing "+commandLine);
                printWriter.println("-------Begin Return------------");
                printWriter.println(returnValue);
                printWriter.println("--------End Return-------------");
                printWriter.println("");
                printWriter.print("Command>");
                printWriter.flush();
            }
        }catch (Exception e){
            log.error("",e);
        }

    }
}
