package com.qzero.server.plugin.utils;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.commands.CommandMethod;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.bridge.PluginCommand;
import com.qzero.server.plugin.bridge.PluginCommandMethod;
import com.qzero.server.plugin.bridge.PluginOperateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PluginCommandLoadUtils {

    private static Logger log= LoggerFactory.getLogger(PluginCommandLoadUtils.class);

    public static List<PluginCommand> loadPluginCommandFromClass(Class cls) throws Exception{
        List<PluginCommand> pluginCommandList=new ArrayList<>();

        Object instance=cls.newInstance();
        Method[] methods=cls.getDeclaredMethods();
        for(Method method:methods){
            PluginCommandMethod commandMethodAnnotation=method.getAnnotation(PluginCommandMethod.class);
            if(commandMethodAnnotation==null)
                continue;

            String commandName=commandMethodAnnotation.commandName();
            if(commandName==null)
                throw new IllegalArgumentException(String.format("Command name for class %s can not be empty", cls.getName()));

            Type[] parameters=method.getParameterTypes();
            if(parameters.length!=4)
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not have 4 parameters(actually it's %d)",
                        method.getName(),commandName,parameters.length));

            if(!(parameters[0].equals(String[].class) &&
                    parameters[1].equals(String.class) &&
                    parameters[2].equals(ServerCommandContext.class)) &&
                    parameters[3].equals(PluginOperateHelper.class))
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not have matched parameter types",
                        method.getName(),commandName));

            if(!method.getReturnType().equals(String.class))
                throw new IllegalArgumentException(String.format("Command method %s for command %s does not take String as return value",
                        method.getName(),commandName));

            PluginCommand command=new PluginCommand() {

                @Override
                public String getCommandName() {
                    return commandMethodAnnotation.commandName();
                }

                @Override
                public int getParameterCount() {
                    return commandMethodAnnotation.parameterCount();
                }

                @Override
                public boolean needServerSelected() {
                    return commandMethodAnnotation.needServerSelected();
                }

                @Override
                public String execute(String[] commandParts, String commandLine, ServerCommandContext context, PluginOperateHelper serverOperateHelper) {
                    try {
                        method.setAccessible(true);
                        return (String) method.invoke(instance,commandParts,commandLine,context,serverOperateHelper);
                    } catch (Exception e){
                        log.error(String.format("Failed to invoke command method %s for command %s", method.getName(),commandName),e);
                        return "Failed to execute command, please contact admin to see logs for detail";
                    }
                }

            };

            pluginCommandList.add(command);

        }

        return pluginCommandList;
    }

}
