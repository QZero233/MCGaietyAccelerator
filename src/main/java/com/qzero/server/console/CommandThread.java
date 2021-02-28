package com.qzero.server.console;

import com.qzero.server.runner.MinecraftServerContainer;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private MinecraftServerContainer container;

    private ServerCommandContext context=new ServerCommandContext();

    private ServerCommandExecutor commandExecutor;

    private boolean listenMode=false;
    private String listenerId=null;

    public CommandThread(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        container=MinecraftServerContainer.getInstance();
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

                if(listenMode){
                    //Exit listen mode
                    container.unregisterOutputListener(context.getCurrentServer(),listenerId);
                    listenerId=null;
                    listenMode=false;

                    printWriter.println("Exit listen mode");
                    printWriter.print("Command>");
                    printWriter.flush();
                    continue;
                }

                if(commandLine.toLowerCase().equals("exit")){
                    running=false;
                    printWriter.println("Command thread stopped, good bye");
                    break;
                }else if(commandLine.toLowerCase().equals("listen")){
                    if(context.getCurrentServer()==null){
                        printWriter.println("No server selected");
                        continue;
                    }

                    printWriter.println("Listen mode is on, no further command input would be taken");
                    printWriter.println("Press enter to exit listen mode");
                    printWriter.flush();

                    listenMode=true;

                    listenerId= UUIDUtils.getRandomUUID();
                    container.registerOutputListener(context.getCurrentServer(), new ServerOutputListener() {
                        @Override
                        public String getListenerId() {
                            return listenerId;
                        }

                        @Override
                        public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
                            if(outputType==OutputType.TYPE_NORMAL)
                                printWriter.println(String.format("[%s]%s", serverName,outputLine));
                            else
                                printWriter.println(String.format("[ERROR][%s]%s", serverName,outputLine));

                            printWriter.flush();
                        }
                    });

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
