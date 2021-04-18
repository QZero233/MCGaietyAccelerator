package com.qzero.server.console.commands;

import com.qzero.server.config.GlobalConfigurationManager;
import com.qzero.server.config.minecraft.MinecraftServerConfiguration;
import com.qzero.server.config.minecraft.MinecraftServerConfigurator;
import com.qzero.server.console.ServerCommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    private GlobalConfigurationManager configurationManager;

    public ConfigurationCommands(){
        configurationManager=GlobalConfigurationManager.getInstance();
    }

    @CommandMethod(commandName = "auto_config",needServerSelected = false,parameterCount = 1)
    private String autoConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        try {
            MinecraftServerConfigurator.configServer(commandParts[1]);
            return "Auto config successfully";
        } catch (IOException e) {
            log.error("Failed to auto config server for "+context.getCurrentServer(),e);
            return "Failed to auto config server for "+context.getCurrentServer();
        }
    }

    @CommandMethod(commandName = "reload",needServerSelected = false)
    private String reloadConfiguration(String[] commandParts, String commandLine, ServerCommandContext context){
        try {
            configurationManager.loadConfig();
            return "Reload successfully";
        }catch (Exception e){
            log.error("Failed to reload server configuration",e);
            return "Failed to reload\n"+e.getMessage();
        }
    }

    /*@CommandMethod(commandName = "show_all_ops",needServerSelected = false)
    private String showAllOPS(String[] commandParts, String commandLine, ServerCommandContext context){
        List<String> opList=configurationManager.getInGameOPIDList();
        if(opList==null || opList.isEmpty())
            return "No in-game op";

        StringBuffer stringBuffer=new StringBuffer();
        for(String op:opList){
            stringBuffer.append(op);
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    @CommandMethod(commandName = "remove_op",needServerSelected = false,parameterCount = 1)
    private String removeOP(String[] commandParts, String commandLine, ServerCommandContext context){
        if(!configurationManager.checkInGameOP(commandParts[1]))
            return String.format("%s is not an op, can not remove it", commandParts[1]);

        try {
            configurationManager.removeInGameOp(commandParts[1]);
            return "Remove successfully";
        } catch (IOException e) {
            log.error("Failed to remove in-game op "+commandParts[1],e);
            return "Failed to remove in-game op "+commandParts[1];
        }
    }

    @CommandMethod(commandName = "add_op",needServerSelected = false,parameterCount = 1)
    private String addOp(String[] commandParts, String commandLine, ServerCommandContext context){
        if(configurationManager.checkInGameOP(commandParts[1]))
            return String.format("%s is already an op, can not add it again", commandParts[1]);

        try {
            configurationManager.addInGameOp(commandParts[1]);
            return "Add successfully";
        } catch (IOException e) {
            log.error("Failed to add in-game op "+commandParts[1],e);
            return "Failed to add in-game op "+commandParts[1];
        }
    }*/

    //TODO get things done,don't forget to check authorize level

    @CommandMethod(commandName = "show_server_config")
    private String showServerConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        MinecraftServerConfiguration configuration=configurationManager.getServerConfigurationManager().getMinecraftServerConfig(context.getCurrentServer());
        StringBuffer result=new StringBuffer();
        result.append(String.format("serverJarFileName=%s\n", configuration.getServerJarFileName()));
        result.append(String.format("javaPath=%s\n", configuration.getJavaPath()));
        result.append(String.format("javaParameter=%s\n", configuration.getJavaParameter()));
        result.append(String.format("autoConfigCopy=%s\n", configuration.getAutoConfigCopy()));

        Map<String,String> customized=configuration.getCustomizedServerProperties();
        Set<String> keySet=customized.keySet();
        for(String key:keySet){
            result.append(key);
            result.append("=");
            result.append(customized.get(key));
            result.append("\n");
        }

        return result.toString();
    }

    @CommandMethod(commandName = "update_server_config",parameterCount = 2)
    private String updateServerConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        String key=commandParts[1];
        String value=commandParts[2];

        try {
            configurationManager.getServerConfigurationManager().updateMinecraftServerConfig(context.getCurrentServer(),key,value);
            return "Update successfully, please reload to apply it";
        } catch (IOException e) {
            log.error("Failed to update server config for "+context.getCurrentServer(),e);
            return "Failed to update server config for "+context.getCurrentServer();
        }
    }

    @CommandMethod(commandName = "add_server",needServerSelected = false,parameterCount = 1)
    private String addServer(String[] commandParts, String commandLine, ServerCommandContext context){
        String serverName=commandParts[1];
        if(configurationManager.getServerConfigurationManager().getMinecraftServerConfig(serverName)!=null)
            return String.format("Server named %s already exists, can not add it", serverName);

        new File(serverName+"/").mkdirs();
        File configFile=new File(serverName+"/serverConfig.config");
        try {
            configFile.createNewFile();
            return "Server created successfully, please reload to apply it";
        } catch (IOException e) {
            log.error("Failed to create config file for new server "+serverName,e);
            return "Failed to create config file for new server "+serverName;
        }
    }

}
