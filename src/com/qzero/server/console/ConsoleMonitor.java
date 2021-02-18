package com.qzero.server.console;

import java.io.*;

public class ConsoleMonitor {

    private Process process;
    private BufferedReader normalOutput;
    private BufferedReader errorOutput;
    private PrintWriter input;


    public ConsoleMonitor(String terminalPath) throws IOException {
        process=new ProcessBuilder(terminalPath).start();
        normalOutput=new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorOutput=new BufferedReader(new InputStreamReader(process.getErrorStream()));
        input=new PrintWriter(process.getOutputStream());
    }

    public ConsoleMonitor(String javaPath, String parameter,File workDir) throws IOException {
        process=Runtime.getRuntime().exec(javaPath+" "+parameter,null,workDir);
        normalOutput=new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorOutput=new BufferedReader(new InputStreamReader(process.getErrorStream()));
        input=new PrintWriter(process.getOutputStream());
    }

    public void executeCommand(String command){
        input.println(command);
        input.flush();
    }

    public String readNormalOutput() throws IOException{
        if(process.isAlive()){
            return normalOutput.readLine();
        }else{
            return null;
        }
    }

    public String readErrorOutput() throws IOException{
        if(process.isAlive()){
            return errorOutput.readLine();
        }else{
            return null;
        }
    }

    public void exit() throws IOException{
        normalOutput.close();
        errorOutput.close();
        input.close();
        process.destroy();
    }

    public void forceStop() throws IOException {
        normalOutput.close();
        errorOutput.close();
        input.close();
        process.destroyForcibly();
    }

}
