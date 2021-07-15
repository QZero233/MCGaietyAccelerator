package com.qzero.server.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleMonitorThread extends Thread {

    private ConsoleMonitor consoleMonitor;

    private boolean running=true;

    private Logger log= LoggerFactory.getLogger(getClass());

    public ConsoleMonitorThread(String terminalPath){
        try {
            consoleMonitor=new ConsoleMonitor(terminalPath);
        } catch (IOException e) {
            log.error("Failed to initialize local console",e);
        }
    }

    @Override
    public void run() {
        super.run();

        //Read normal output
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    String msg;
                    while ((msg=consoleMonitor.readNormalOutput())!=null){
                        System.out.println(msg);
                    }
                }catch (Exception e){
                    log.error("Failed to read console normal output",e);
                }
            }
        }.start();

        //Read error output
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    String msg;
                    while ((msg=consoleMonitor.readErrorOutput())!=null){
                        System.err.println(msg);
                    }
                }catch (Exception e){
                    log.error("Failed to read console normal output",e);
                }
            }
        }.start();

        Scanner scanner=new Scanner(System.in);

        try {
            while (running){
                System.out.print("Console>");
                String command=scanner.nextLine();

                consoleMonitor.executeCommand(command);
            }
        }catch (Exception e){
            log.error("Failed to read console command input",e);
            log.error("Local console terminated");
        }

    }

}
