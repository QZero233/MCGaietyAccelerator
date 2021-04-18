package com.qzero.server.config.manager;

import com.qzero.server.config.IConfigurationManager;
import com.qzero.server.runner.MinecraftServerContainerSession;
import com.qzero.server.utils.ConfigurationUtils;

import java.io.File;
import java.util.Map;

public class ServerManagerConfigurationManager implements IConfigurationManager {

    private ServerManagerConfiguration managerConfiguration;

    public static final String MANAGER_CONFIG_FILE_NAME="manager.config";

    @Override
    public void loadConfig() throws Exception {
        File file=new File(MANAGER_CONFIG_FILE_NAME);
        if(!file.exists())
            throw new IllegalStateException("Manager config file does not exist");

        Map<String,String> config= ConfigurationUtils.readConfiguration(file);
        if(config==null)
            throw new IllegalStateException("Manager config file can not be empty");

        managerConfiguration=ConfigurationUtils.configToJavaBeanWithOnlyStringFields(config,ServerManagerConfiguration.class);
        if(managerConfiguration.getServerType()==null)
            managerConfiguration.setServerType(ServerManagerConfiguration.SERVER_TYPE_COMMON);

        switch (managerConfiguration.getServerType()){
            case ServerManagerConfiguration.SERVER_TYPE_COMMON:
                managerConfiguration.setContainerType(MinecraftServerContainerSession.ContainerType.COMMON);
                break;
            case ServerManagerConfiguration.SERVER_TYPE_SINGLE_PORT:
                managerConfiguration.setContainerType(MinecraftServerContainerSession.ContainerType.SINGLE_PORT);
                break;
            default:
                throw new IllegalArgumentException("Unknown server type called "+managerConfiguration.getServerType());
        }

        if(managerConfiguration.getRemoteConsolePort()==null || managerConfiguration.getRemoteConsolePort().equals("0")){
            managerConfiguration.setEnableRemoteConsole(true);
        }else{
            managerConfiguration.setEnableRemoteConsole(true);
            managerConfiguration.setRemoteConsolePortInInt(Integer.parseInt(managerConfiguration.getRemoteConsolePort()));
        }

        if(managerConfiguration.getSinglePort()!=null){
            managerConfiguration.setSinglePortInInt(Integer.parseInt(managerConfiguration.getSinglePort()));
        }
    }

    public ServerManagerConfiguration getManagerConfiguration() {
        return managerConfiguration;
    }

}
