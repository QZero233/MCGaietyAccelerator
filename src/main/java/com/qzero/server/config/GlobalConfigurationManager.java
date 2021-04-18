package com.qzero.server.config;

import com.qzero.server.runner.MinecraftServerContainerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It will load config when start
 * It won't interact with file system unless loadConfig is called
 */
public class GlobalConfigurationManager {

    private Logger log= LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_SERVER_JAR_NAME="server.jar";
    public static final String EULA_FILE_NAME="eula.txt";
    public static final String DEFAULT_SERVER_PROPERTIES_NAME="server.properties";

    public static final String ENV_CONFIG_FILE_NAME="env.config";

    public static final String SERVER_CONFIG_FILE_NAME="serverConfig.config";

    public static final String AUTHORIZE_CONFIG_FILE_DIR="authorize/";

    public static final String IN_GAME_OP_ID_FILE_NAME="inGameOPID.config";

    public static final String MANAGER_CONFIG_FILE_NAME="manager.config";

    static {
        new File(AUTHORIZE_CONFIG_FILE_DIR).mkdirs();
    }

    private static GlobalConfigurationManager instance;

    private ServerEnvironment environment;
    private Map<String,MinecraftServerConfiguration> mcServers=new HashMap<>();

    private ServerManagerConfiguration managerConfiguration;

    private List<String> inGameOPIDList=new ArrayList<>();

    private GlobalConfigurationManager(){}

    public static GlobalConfigurationManager getInstance() {
        if(instance==null)
            instance=new GlobalConfigurationManager();
        return instance;
    }

    private void loadServerEnvironment() throws IOException, InstantiationException, IllegalAccessException {
        //Load server environment
        File envFile=new File(ENV_CONFIG_FILE_NAME);
        Map<String,String> envConfig=ConfigurationUtils.readConfiguration(envFile);

        if(envConfig==null)
            throw new IllegalStateException("Server environment configuration is empty");

        environment=ConfigurationUtils.configToJavaBeanWithOnlyStringFields(envConfig,ServerEnvironment.class);

        //Load java absolute path
        File javaFile=new File(environment.getJavaPath());
        if(javaFile.exists())
            environment.setJavaPath(javaFile.getAbsolutePath());

        //Load java parameter
        if(environment.getJavaParameter()==null)
            environment.setJavaParameter("");

    }

    private void loadMinecraftServers() throws IOException, InstantiationException, IllegalAccessException {
        //Load minecraft servers
        File currentDir=new File("");
        currentDir=currentDir.getAbsoluteFile();
        File[] fileArray=currentDir.listFiles();
        for(File file:fileArray){
            if(!file.isDirectory())
                continue;

            String[] serverConfigFileName=file.list((File dir,String name) -> name.equals("serverConfig.config"));//FIXME 远程服务器上扫不到
            if(serverConfigFileName==null || serverConfigFileName.length==0)
                continue;


            String serverName=file.getName();
            log.debug("Found Minecraft server "+serverName);

            File configurationFile=new File(serverName+"/"+SERVER_CONFIG_FILE_NAME);

            Map<String,String> config=ConfigurationUtils.readConfiguration(configurationFile);
            MinecraftServerConfiguration configuration=ConfigurationUtils.configToJavaBeanWithOnlyStringFields(config,MinecraftServerConfiguration.class);

            configuration.setServerName(serverName);

            if(!config.containsKey("serverJarFileName"))
                configuration.setServerJarFileName(DEFAULT_SERVER_JAR_NAME);
            if(!config.containsKey("javaPath"))
                configuration.setJavaPath(environment.getJavaPath());
            if(!config.containsKey("javaParameter"))
                configuration.setJavaParameter(environment.getJavaParameter());

            config.remove("serverJarFileName");
            config.remove("javaPath");
            config.remove("javaParameter");
            config.remove("autoConfigCopy");

            configuration.setCustomizedServerProperties(config);

            mcServers.put(serverName,configuration);

        }
    }

    private void loadAuthorizeInfo() throws IOException {
        //TODO Load authorize config
        File idFile=new File(AUTHORIZE_CONFIG_FILE_DIR+IN_GAME_OP_ID_FILE_NAME);
        if(idFile.exists()){
            inGameOPIDList=ConfigurationUtils.readListConfiguration(idFile);
        }
    }

    private void loadManagerConfig() throws IOException, InstantiationException, IllegalAccessException {
        File file=new File(MANAGER_CONFIG_FILE_NAME);
        if(!file.exists())
            throw new IllegalStateException("Manager config file does not exist");

        Map<String,String> config=ConfigurationUtils.readConfiguration(file);
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

    public void loadConfig() throws IOException, InstantiationException, IllegalAccessException {
        loadServerEnvironment();

        loadMinecraftServers();

        loadAuthorizeInfo();

        loadManagerConfig();
    }

    public ServerManagerConfiguration getManagerConfiguration() {
        return managerConfiguration;
    }

    public ServerEnvironment getServerEnvironment() {
        return environment;
    }

    public MinecraftServerConfiguration getMinecraftServerConfig(String serverName){
        return mcServers.get(serverName);
    }

    public Map<String,MinecraftServerConfiguration> getMcServers(){
        return mcServers;
    }

    public boolean checkInGameOP(String id){
        return inGameOPIDList.contains(id);
    }

    public List<String> getInGameOPIDList(){
        return inGameOPIDList;
    }

    public void removeInGameOp(String opId) throws IOException {
        inGameOPIDList.remove(opId);
        ConfigurationUtils.writeListConfiguration(inGameOPIDList,new File(AUTHORIZE_CONFIG_FILE_DIR+IN_GAME_OP_ID_FILE_NAME));
    }

    public void addInGameOp(String opId) throws IOException {
        inGameOPIDList.add(opId);
        ConfigurationUtils.writeListConfiguration(inGameOPIDList,new File(AUTHORIZE_CONFIG_FILE_DIR+IN_GAME_OP_ID_FILE_NAME));
    }

    public void updateMinecraftServerConfig(String serverName,String key,String value) throws IOException {
        File configurationFile=new File(serverName+"/"+SERVER_CONFIG_FILE_NAME);
        ConfigurationUtils.updateConfiguration(configurationFile,key,value);
    }

}
