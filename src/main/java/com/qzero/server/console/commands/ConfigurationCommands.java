package com.qzero.server.console.commands;

import com.qzero.server.SpringUtil;
import com.qzero.server.config.MinecraftServerConfig;
import com.qzero.server.config.StartConfig;
import com.qzero.server.console.ServerCommandContext;
import com.qzero.server.data.ServerAdmin;
import com.qzero.server.service.AdminAccountService;
import com.qzero.server.service.MinecraftConfigService;
import com.qzero.server.utils.SHA256Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationCommands {

    private Logger log= LoggerFactory.getLogger(getClass());

    private MinecraftConfigService minecraftConfigService;

    private AdminAccountService adminAccountService;

    public ConfigurationCommands(){
        minecraftConfigService=SpringUtil.getBean(MinecraftConfigService.class);
        adminAccountService= SpringUtil.getBean(AdminAccountService.class);
    }

    @CommandMethod(commandName = "auto_config",needServerSelected = false,parameterCount = 1)
    private String autoConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        try {
            MinecraftServerConfig config=minecraftConfigService.getConfig(commandParts[1]);
            minecraftConfigService.applyConfigForServer(config);
            return "Auto config successfully";
        } catch (Exception e) {
            log.error("Failed to auto config server for "+context.getCurrentServer(),e);
            return "Failed to auto config server for "+context.getCurrentServer();
        }
    }

    @CommandMethod(commandName = "show_all_admins",needServerSelected = false,minAdminPermission = ServerAdmin.LEVEL_SUPER)
    private String showAllAdmins(String[] commandParts, String commandLine, ServerCommandContext context){
        List<String> adminNameList=adminAccountService.getAdminNameList();
        if(adminNameList==null || adminNameList.isEmpty())
            return "No in-game admin";

        StringBuffer stringBuffer=new StringBuffer();
        for(String op:adminNameList){
            stringBuffer.append(op);
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    @CommandMethod(commandName = "remove_admin",needServerSelected = false,parameterCount = 1,minAdminPermission = ServerAdmin.LEVEL_SUPER)
    private String removeAdmin(String[] commandParts, String commandLine, ServerCommandContext context){
        if(context.getOperatorId().equals(commandParts[1]))
            return "You can not remove yourself";

        if(!adminAccountService.hasAdmin(commandParts[1]))
            return String.format("%s is not an admin, can not remove it", commandParts[1]);

        try {
            adminAccountService.removeAdmin(commandParts[1]);
            return "Remove successfully";
        } catch (Exception e) {
            log.error("Failed to remove in-game op "+commandParts[1],e);
            return "Failed to remove in-game op "+commandParts[1];
        }
    }

    @CommandMethod(commandName = "add_admin",needServerSelected = false,parameterCount = 4,minAdminPermission = ServerAdmin.LEVEL_SUPER)
    private String addAdmin(String[] commandParts, String commandLine, ServerCommandContext context){
        if(adminAccountService.hasAdmin(commandParts[1]))
            return String.format("%s is already an op, can not add it again", commandParts[1]);

        if(!commandParts[3].equals(commandParts[4]))
            return "The two passwords do not match, please check";

        try {
            ServerAdmin serverAdminAdd=new ServerAdmin();
            serverAdminAdd.setMinecraftId(commandParts[1]);
            serverAdminAdd.setAdminLevel(Integer.parseInt(commandParts[2]));
            serverAdminAdd.setPasswordHash(SHA256Utils.getHexEncodedSHA256(commandParts[3]));

            adminAccountService.addAdmin(serverAdminAdd);
            return "Add successfully";
        } catch (Exception e) {
            log.error("Failed to add in-game op "+commandParts[1],e);
            return "Failed to add in-game op "+commandParts[1];
        }
    }

    /**
     * update_admin_password admin_name new_password confirm_new_password
     */
    @CommandMethod(commandName = "update_admin_password",needServerSelected = false,parameterCount = 3,minAdminPermission = ServerAdmin.LEVEL_SUPER)
    private String updateAdminPassword(String[] commandParts, String commandLine, ServerCommandContext context){
        String adminName=commandParts[1];
        if(!adminAccountService.hasAdmin(adminName))
            return "No admin named "+adminName;

        if(!commandParts[2].equals(commandParts[3]))
            return "The two passwords do not match, please check";

        adminAccountService.updatePassword(adminName,SHA256Utils.getHexEncodedSHA256(commandParts[2]));
        return "Update password successfully";
    }

    @CommandMethod(commandName = "show_server_config")
    private String showServerConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        try {
            MinecraftServerConfig config=minecraftConfigService.getConfig(context.getCurrentServer());

            StringBuffer result=new StringBuffer();
            Map<String,String> customized=config.getCustomizedServerProperties();
            Set<String> keySet=customized.keySet();
            for(String key:keySet){
                result.append(key);
                result.append("=");
                result.append(customized.get(key));
                result.append("\n");
            }

            return result.toString();
        }catch (Exception e){
            log.error("Failed to read server config for server "+context.getCurrentServer(),e);
            return "Failed to read config, reason:"+e.getMessage();
        }
    }

    @CommandMethod(commandName = "update_server_config",parameterCount = 2)
    private String updateServerConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        String key=commandParts[1];
        String value=commandParts[2];

        try {
            minecraftConfigService.updateServerConfig(context.getCurrentServer(),key,value);
            return "Update successfully, please reload to apply it";
        } catch (Exception e) {
            log.error("Failed to update server config for "+context.getCurrentServer(),e);
            return "Failed to update server config for "+context.getCurrentServer();
        }
    }

    @CommandMethod(commandName = "add_server",needServerSelected = false,parameterCount = 1)
    private String addServer(String[] commandParts, String commandLine, ServerCommandContext context){
        String serverName=commandParts[1];
        try {
            minecraftConfigService.newEmptyServer(serverName);
            return "Server created successfully, please reload to apply it";
        } catch (Exception e) {
            log.error("Failed to create config file for new server "+serverName,e);
            return "Failed to create config file for new server "+serverName;
        }
    }

    @CommandMethod(commandName = "show_start_config",needServerSelected = false)
    private String showStartConfig(String[] commandParts, String commandLine, ServerCommandContext context){
        StartConfig startConfig=SpringUtil.getBean(StartConfig.class);
        String configInString=startConfig.toString();
        configInString=configInString.replaceAll("StartConfig\\{","");
        configInString=configInString.replaceAll("\\}","");
        configInString=configInString.replaceAll(",","\n");

        return configInString;
    }

}
