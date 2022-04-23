package com.qzero.server.plugin.utils;

import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.console.commands.CommandMethod;
import com.qzero.server.console.commands.ConsoleCommand;
import com.qzero.server.plugin.PluginComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PluginCommandLoadUtils {

    private static Logger log= LoggerFactory.getLogger(PluginCommandLoadUtils.class);

    public static void loadPluginCommandFromClass(Class cls, PluginComponentRegistry registry) throws Exception{
        Object instance=cls.getDeclaredConstructor().newInstance();
        Method[] methods=cls.getDeclaredMethods();
        for(Method method:methods){
            CommandMethod commandMethodAnnotation=method.getAnnotation(CommandMethod.class);
            if(commandMethodAnnotation==null)
                continue;

            String commandName=commandMethodAnnotation.commandName();
            if(commandName==null)
                throw new IllegalArgumentException(String.format("Command name for class %s can not be empty", cls.getName()));

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


            registry.addCommand(commandName,new ConsoleCommand() {
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

}
