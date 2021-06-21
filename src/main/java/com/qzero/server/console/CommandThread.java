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

    private boolean local=false;

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

    public CommandThread(InputStream is, OutputStream os, String operatorId,boolean local) {
        this.is = is;
        this.os = os;
        this.local=local;

        commandExecutor=ServerCommandExecutor.getInstance();
        context.setOperatorId(operatorId);
    }

    public CommandThread(InputStream is, OutputStream os, String operatorId) {
        this(is,os,operatorId,false);
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


                if(commandLine.toLowerCase().equals("stop_program")){
                    if(!local){
                        printWriter.println("This command can only be used in local console");
                        printWriter.print("Command>");
                        printWriter.flush();
                        continue;
                    }

                    printWriter.print("This will force stop program\nplease make sure all the servers are closed\n" +
                            "Otherwise there will be data loss\nDo you still want to stop program?(y/n)");
                    printWriter.flush();
                    String choice=scanner.nextLine();
                    switch (choice.toLowerCase()){
                        case "y":
                            printWriter.println("Program force stopped");
                            printWriter.flush();
                            System.exit(0);
                        case "n":
                            printWriter.println("You cancelled the action");
                            printWriter.print("Command>");
                            printWriter.flush();
                            break;
                        default:
                            printWriter.println("Wrong input");
                            printWriter.print("Command>");
                            printWriter.flush();
                            break;
                    }

                    continue;
                }

                if(commandLine.toLowerCase().equals("exit")){
                    if(local){
                        printWriter.println("Local console can not use exit,please use stop_program instead");
                        printWriter.print("Command>");
                        printWriter.flush();
                        continue;
                    }
                    running=false;
                    printWriter.println("Command thread stopped, good bye");
                    printWriter.flush();
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
