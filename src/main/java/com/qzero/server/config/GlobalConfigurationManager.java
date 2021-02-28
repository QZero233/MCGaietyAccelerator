package com.qzero.server.config;

import com.qzero.server.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * It will load config when start
 * It won't interact with file system unless loadConfig is called
 */
public class GlobalConfigurationManager {

    public static final String DEFAULT_SERVER_JAR_NAME="server.jar";
    public static final String EULA_FILE_NAME="eula.txt";
    public static final String DEFAULT_SERVER_PROPERTIES_NAME="server.properties";

    public static final String ENV_CONFIG_FILE_NAME="env.config";

    public static final String SERVER_CONFIG_FILE_NAME="serverConfig.config";

    public static final String AUTHORIZE_CONFIG_FILE_DIR="authorize/";

    public static final String IN_GAME_OP_ID_FILE_NAME="inGameOPID.config";

    static {
        new File(AUTHORIZE_CONFIG_FILE_DIR).mkdirs();
    }

    private static GlobalConfigurationManager instance;

    private ServerEnvironment environment;
    private Map<String,MinecraftServerConfiguration> mcServers=new HashMap<>();

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
    }

    private void loadMinecraftServers() throws IOException, InstantiationException, IllegalAccessException {
        //Load minecraft servers
        File currentDir=new File(".");
        File[] fileArray=currentDir.listFiles();
        for(File file:fileArray){
            if(!file.isDirectory())
                continue;

            String[] serverConfigFileName=file.list((File dir,String name) -> name.equals("serverConfig.config"));
            if(serverConfigFileName==null || serverConfigFileName.length==0)
                continue;


            String serverName=file.getName();

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

            if(config.containsKey("needConfig") && config.get("needConfig").toLowerCase().equals("true")){
                configuration.setNeedConfig(true);
            }

            config.remove("serverJarFileName");
            config.remove("javaPath");
            config.remove("javaParameter");

            configuration.setCustomizedServerProperties(config);

            mcServers.put(serverName,configuration);

        }
    }

    private void loadAuthorizeInfo() throws IOException {
        //TODO Load authorize config
        File idFile=new File(AUTHORIZE_CONFIG_FILE_DIR+IN_GAME_OP_ID_FILE_NAME);
        if(idFile.exists()){
            String idText=new String(StreamUtils.readFile(idFile));
            String[] idArray=idText.split("\n");
            inGameOPIDList= Arrays.asList(idArray);
        }
    }

    public void loadConfig() throws IOException, InstantiationException, IllegalAccessException {
        loadServerEnvironment();

        loadMinecraftServers();

        loadAuthorizeInfo();
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

    public void updateMinecraftServerConfig(String serverName,String key,String value) throws IOException {
        File configurationFile=new File(serverName+"/"+SERVER_CONFIG_FILE_NAME);
        ConfigurationUtils.updateConfiguration(configurationFile,key,value);
    }

}
