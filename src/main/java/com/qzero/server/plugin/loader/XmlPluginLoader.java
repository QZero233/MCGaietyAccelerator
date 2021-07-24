package com.qzero.server.plugin.loader;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.ServerCommandExecutor;
import com.qzero.server.plugin.bridge.PluginCommand;
import com.qzero.server.plugin.bridge.PluginEntry;
import com.qzero.server.plugin.bridge.PluginOperateHelper;
import com.qzero.server.runner.ServerOutputListener;
import com.qzero.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlPluginLoader implements PluginLoader {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public PluginEntry loadPlugin(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            List<PluginCommand> commandList=loadCommands(document);
            List<ServerOutputListener> listenerList=loadListeners(document);

            return new PluginEntry() {
                @Override
                public void initializePluginCommandsAndListeners() {

                }

                @Override
                public void onPluginLoaded() {

                }

                @Override
                public void onPluginUnloaded() {

                }

                @Override
                public List<PluginCommand> getPluginCommands() {
                    return commandList;
                }

                @Override
                public List<ServerOutputListener> getPluginListeners() {
                    return listenerList;
                }
            };
        }catch (Exception e){
            log.error("Failed to load plugin "+file.getName(),e);
            return null;
        }
    }

    private List<PluginCommand> loadCommands(Document document){
        NodeList nodeList=document.getElementsByTagName("commands");
        Node commandsNode = null;
        for(int i=0;i<nodeList.getLength();i++){
            commandsNode=nodeList.item(i);
            if(commandsNode.getNodeType()==Node.ELEMENT_NODE)
                break;
        }

        Element commandsElement= (Element) commandsNode;
        nodeList=commandsElement.getElementsByTagName("command");

        List<PluginCommand> commandList=new ArrayList<>();

        for(int i=0;i<nodeList.getLength();i++){
            Node node=nodeList.item(i);
            if(node.getNodeType()!=Node.ELEMENT_NODE)
                continue;

            Element element= (Element) node;
            String commandName=element.getAttribute("name");
            String parameterCountString=element.getAttribute("parameterCount");
            int parameterCount=Integer.parseInt(parameterCountString);

            List<String> executeCommandList=loadExecuteCommands(element.getElementsByTagName("execute"));

            PluginCommand command=new PluginCommand() {
                @Override
                public String getCommandName() {
                    return commandName;
                }

                @Override
                public String execute(String[] commandParts, String commandLine, ServerCommandContext context, PluginOperateHelper serverOperateHelper) {
                    try {
                        ServerCommandContext commandContext=new ServerCommandContext();
                        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();
                        for(String command:executeCommandList){
                            String newCommand=replaceParameters(command,commandParts);
                            executor.executeCommand(newCommand,commandContext);
                        }
                        return "Command executed successfully";
                    }catch (Exception e){
                        log.error("Failed to execute xml plugin command "+commandName,e);
                        return "Failed to execute xml plugin command "+commandName;
                    }
                }

                @Override
                public int getParameterCount() {
                    return parameterCount;
                }

                @Override
                public boolean needServerSelected() {
                    return false;
                }
            };

            commandList.add(command);
        }

        return commandList;
    }

    private List<ServerOutputListener> loadListeners(Document document){
        NodeList nodeList=document.getElementsByTagName("listeners");
        Node commandsNode = null;
        for(int i=0;i<nodeList.getLength();i++){
            commandsNode=nodeList.item(i);
            if(commandsNode.getNodeType()==Node.ELEMENT_NODE)
                break;
        }

        Element commandsElement= (Element) commandsNode;
        nodeList=commandsElement.getElementsByTagName("listener");


        List<ServerOutputListener> listenerList=new ArrayList<>();

        for(int i=0;i<nodeList.getLength();i++){
            Node node=nodeList.item(i);
            if(node.getNodeType()!=Node.ELEMENT_NODE)
                continue;

            Element element= (Element) node;
            String listenType=element.getAttribute("type");

            if(!listenType.equalsIgnoreCase("output") &&
            !listenType.equalsIgnoreCase("serverEvent") &&
            !listenType.equalsIgnoreCase("playerEvent"))
                throw new IllegalArgumentException(String.format("The listener type(%s) is illegal", listenType));

            List<String> executeCommandList=loadExecuteCommands(element.getElementsByTagName("execute"));

            ServerOutputListener listener=new ServerOutputListener() {
                private String id= UUIDUtils.getRandomUUID();

                @Override
                public String getListenerId() {
                    return id;
                }

                @Override
                public void receivedOutputLine(String serverName, String outputLine, OutputType outputType) {
                    if(!listenType.equalsIgnoreCase("output"))
                        return;

                    try {
                        ServerCommandContext commandContext=new ServerCommandContext();
                        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

                        String outputTypeString=null;
                        switch (outputType){
                            case TYPE_NORMAL:
                                outputTypeString="normal";
                                break;
                            case TYPE_ERROR:
                                outputTypeString="error";
                                break;
                        }
                        String[] commandParameters=new String[]{"",serverName,outputLine,outputTypeString};
                        for(String command:executeCommandList){
                            String newCommand=replaceParameters(command,commandParameters);
                            executor.executeCommand(newCommand,commandContext);
                        }
                    }catch (Exception e){
                        log.error("Failed to execute xml plugin listener method",e);
                    }
                }

                @Override
                public void receivedServerEvent(String serverName, ServerEvent event) {
                    if(!listenType.equalsIgnoreCase("serverEvent"))
                        return;

                    try {
                        ServerCommandContext commandContext=new ServerCommandContext();
                        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

                        String eventString=null;
                        switch (event){
                            case SERVER_STARTING:
                                eventString="starting";
                                break;
                            case SERVER_STARTED:
                                eventString="started";
                                break;
                            case SERVER_STOPPED:
                                eventString="stopped";
                                break;
                        }
                        String[] commandParameters=new String[]{"",serverName,eventString};
                        for(String command:executeCommandList){
                            String newCommand=replaceParameters(command,commandParameters);
                            executor.executeCommand(newCommand,commandContext);
                        }
                    }catch (Exception e){
                        log.error("Failed to execute xml plugin listener method",e);
                    }
                }

                @Override
                public void receivedPlayerEvent(String serverName, String playerName, PlayerEvent event) {
                    if(!listenType.equalsIgnoreCase("playerEvent"))
                        return;

                    try {
                        ServerCommandContext commandContext=new ServerCommandContext();
                        ServerCommandExecutor executor=ServerCommandExecutor.getInstance();

                        String eventString=null;
                        switch (event){
                            case JOIN:
                                eventString="join";
                                break;
                            case LEAVE:
                                eventString="leave";
                                break;
                        }
                        String[] commandParameters=new String[]{"",serverName,playerName,eventString};
                        for(String command:executeCommandList){
                            String newCommand=replaceParameters(command,commandParameters);
                            executor.executeCommand(newCommand,commandContext);
                        }
                    }catch (Exception e){
                        log.error("Failed to execute xml plugin listener method",e);
                    }
                }
            };

            listenerList.add(listener);
        }

        return listenerList;
    }

    private List<String> loadExecuteCommands(NodeList executeNodeList){
        List<String> executeCommandList=new ArrayList<>();
        for(int j=0;j<executeNodeList.getLength();j++){
            Node executeNode=executeNodeList.item(j);
            if(executeNode.getNodeType()!=Node.ELEMENT_NODE)
                continue;

            Element executeElement= (Element) executeNode;
            executeCommandList.add(executeElement.getFirstChild().getNodeValue());
        }

        return executeCommandList;
    }

    private String replaceParameters(String originCommandLine,String[] commandParts){
        int parameterCount=commandParts.length-1;
        for(int i=1;i<=parameterCount;i++){
            originCommandLine=originCommandLine.replaceAll("\\$"+i+"\\$",commandParts[i]);
        }
        return originCommandLine;
    }

}
