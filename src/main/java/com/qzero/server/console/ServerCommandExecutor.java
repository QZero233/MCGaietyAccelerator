package com.qzero.server.console;

import com.qzero.server.SpringUtil;
import com.qzero.server.console.commands.*;
import com.qzero.server.data.ServerAdmin;
import com.qzero.server.service.AdminAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCommandExecutor {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Map<String, ConsoleCommand> commandMap=new HashMap<>();

    private static ServerCommandExecutor instance;

    private AdminAccountService adminAccountService;

    public static ServerCommandExecutor getInstance(){
        if(instance==null)
            instance=new ServerCommandExecutor();
        return instance;
    }

    private ServerCommandExecutor(){
        adminAccountService= SpringUtil.getBean(AdminAccountService.class);
    }

    public void loadCommands() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        loadCommandsFor(EnvironmentCommands.class);
        loadCommandsFor(ServerManageCommands.class);
        loadCommandsFor(ConfigurationCommands.class);
        loadCommandsFor(PluginCommands.class);
    }

    private void loadCommandsFor(Class cls) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object instance=cls.getDeclaredConstructor().newInstance();
        Method[] methods=cls.getDeclaredMethods();
        for(Method method:methods){
            CommandMethod commandMethodAnnotation=method.getAnnotation(CommandMethod.class);
            if(commandMethodAnnotation==null)
                continue;

            String commandName=commandMethodAnnotation.commandName();
            if(commandName==null)
                throw new IllegalArgumentException(String.format("Command name for class %s can not be empty", cls.getName()));

            if(commandMap.containsKey(commandName))
                throw new IllegalArgumentException(String.format("Command %s has more than one implements", commandName));

            Type[] parameters=method.getParameterTypes();
            if(parameters.length!=3)
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not have 3 parameters(actually it's %d)",
                        method.getName(),commandName,parameters.length));

            if(!(parameters[0].equals(String[].class) &&
            parameters[1].equals(String.class) && parameters[2].equals(ServerCommandContext.class)))
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not have matched parameter types",
                        method.getName(),commandName));

            if(!method.getReturnType().equals(String.class))
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not take String as return value",
                        method.getName(),commandName));


            commandMap.put(commandName, new ConsoleCommand() {
                @Override
                public int getCommandParameterCount() {
                    return commandMethodAnnotation.parameterCount();
                }

                @Override
                public boolean needServerSelected() {
                    return commandMethodAnnotation.needServerSelected();
                }

                @Override
                public int minAdminPermission() {
                    return commandMethodAnnotation.minAdminPermission();
                }

                @Override
                public String execute(String[] commandParts, String fullCommand, ServerCommandContext context) {
                    try {
                        method.setAccessible(true);
                        return (String) method.invoke(instance,commandParts,fullCommand,context);
                    } catch (Exception e){
                        log.error(String.format("Failed to invoke command method %s for command %s", method.getName(),commandName),e);
                        return "Failed to execute command, please contact admin to see logs for detail";
                    }
                }
            });
        }
    }

    public void addCommand(String commandName,ConsoleCommand consoleCommand){
        if(commandMap.containsKey(commandName))
            throw new IllegalArgumentException("Already have a command named "+commandName);

        if(consoleCommand==null)
            throw new NullPointerException("Can not take an empty command implement");

        commandMap.put(commandName,consoleCommand);
    }

    private String[] splitCommand(String commandLine){
        byte[] buf=commandLine.getBytes();
        List<String> commandParts=new ArrayList<>();


        ByteArrayOutputStream current=new ByteArrayOutputStream();
        //ByteOutputStream current=new ByteOutputStream();
        boolean whole=false;
        boolean escape=false;
        for(byte b:buf) {
            if (escape) {
                current.write(b);
                escape = false;
                continue;
            }

            if (b == '\\') {
                escape = true;
                continue;
            }

            if (b == '\"') {
                whole = !whole;
                continue;
            }

            if (b == ' ') {
                if (whole) {
                    current.write(b);
                } else {
                    commandParts.add(new String(current.toByteArray(),0, current.size()));
                    current = new ByteArrayOutputStream();
                }
                continue;
            }

            current.write(b);
        }

        commandParts.add(new String(current.toByteArray(),0, current.size()));

        return commandParts.toArray(new String[]{});
    }

    public String executeCommand(String commandLine,ServerCommandContext context){
        String[] parts=splitCommand(commandLine);
        String commandName=parts[0];
        if(!commandMap.containsKey(commandName))
            return "Unknown command called "+commandName;

        ConsoleCommand consoleCommand=commandMap.get(commandName);

        //Check parameter count
        int parameterCount=consoleCommand.getCommandParameterCount();
        if(parts.length-1<parameterCount)
            return String.format("Command %s need as least %d parameters, but there are only %d", commandName,
                    parameterCount,parts.length-1);

        //Check if need selected server
        if(consoleCommand.needServerSelected() && context.getCurrentServer()==null)
            return "No server selected";

        //Check permission
        int minAdminLevel=consoleCommand.minAdminPermission();
        //Only level greater or equal than 0 needs check
        //Skip local console
        if(minAdminLevel>=0 && context.getEnvType()!= ServerCommandContext.ExecuteEnvType.LOCAL_CONSOLE){
            if(context.getOperatorId()==null){
                return "Not login yet, can not execute command "+commandName;
            }

            if(!adminAccountService.hasAdmin(context.getOperatorId())){
                return "You are not an admin, can not execute command "+commandName;
            }

            ServerAdmin admin=adminAccountService.getAdminInfo(context.getOperatorId());
            if(admin.getAdminLevel()<minAdminLevel){
                return "You have no permission to execute command "+commandName;
            }
        }

        return consoleCommand.execute(parts,commandLine,context);
    }

    public void unloadCommand(String commandName){
        commandMap.remove(commandName);
    }

}
