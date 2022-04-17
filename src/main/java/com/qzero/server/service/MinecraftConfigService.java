package com.qzero.server.service;

import com.qzero.server.config.DefaultEnvConfig;
import com.qzero.server.config.MinecraftServerConfig;
import com.qzero.server.exception.MinecraftServerConfigException;
import com.qzero.server.exception.MinecraftServerNotFoundException;
import com.qzero.server.utils.ConfigurationUtils;
import com.qzero.server.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MinecraftConfigService {

    private Logger log = LoggerFactory.getLogger(getClass());

    public static final String EULA_FILE_NAME = "eula.txt";
    public static final String SERVER_PROPERTIES_NAME = "server.properties";

    public static final String SERVER_CONFIG_FILE_NAME = "serverConfig.config";

    @Autowired
    private DefaultEnvConfig defaultEnvConfig;

    public MinecraftServerConfig getConfig(String serverName) throws Exception{
        File serverConfigFile=new File(serverName+"/"+SERVER_CONFIG_FILE_NAME);
        if(!serverConfigFile.exists())
            throw new MinecraftServerNotFoundException(serverName,"get config");

        //Load config from file system
        Map<String, String> config = ConfigurationUtils.readConfiguration(serverConfigFile);

        //Convert to java bean
        //Inject default values first
        MinecraftServerConfig minecraftServerConfig=new MinecraftServerConfig();
        minecraftServerConfig.setServerName(serverName);
        minecraftServerConfig.setJavaPath(defaultEnvConfig.getJavaPath());
        minecraftServerConfig.setJavaParameter(defaultEnvConfig.getJavaParameter());
        minecraftServerConfig.setServerJarFileName(defaultEnvConfig.getServerJarFileName());

        //Then convert
        minecraftServerConfig=ConfigurationUtils.configToJavaBeanWithOnlyStringFields(config, minecraftServerConfig);

        minecraftServerConfig.setCustomizedServerProperties(config);

        return minecraftServerConfig;
    }

    public boolean isServerExist(String serverName){
        return new File(serverName+"/"+SERVER_CONFIG_FILE_NAME).exists();
    }

    public void newEmptyServer(String serverName) throws Exception{
        File serverConfigFile=new File(serverName+"/"+SERVER_CONFIG_FILE_NAME);
        if(serverConfigFile.exists())
            throw new IllegalArgumentException(String.format("Server with name %s exists, can not new one", serverName));

        new File(serverName+"/").mkdirs();
        serverConfigFile.createNewFile();
    }

    public void updateServerConfig(String serverName, String key, String value) throws Exception{
        File configurationFile = new File(serverName + "/" + SERVER_CONFIG_FILE_NAME);
        ConfigurationUtils.updateConfiguration(configurationFile, key, value);
    }

    public List<String> getAllServers(){
        List<String> result=new ArrayList<>();

        File currentDir = new File("");
        currentDir = currentDir.getAbsoluteFile();
        File[] fileArray = currentDir.listFiles();
        for (File file : fileArray) {
            if (!file.isDirectory())
                continue;

            String[] serverConfigFileName = file.list((File dir, String name) -> name.equals("serverConfig.config"));
            if (serverConfigFileName == null || serverConfigFileName.length == 0)
                continue;

            result.add(file.getName());
            log.trace("Found server "+file.getName());
        }

        return result;
    }

    /**
     * Check if the server with the given config can run normally
     * If no exception thrown, the server is runnable
     */
    public void checkIfMinecraftServerRunnable(MinecraftServerConfig config) throws Exception {
        String serverName=config.getServerName();
        File serverDir=new File(serverName+"/");

        File serverJarFile=new File(serverDir,config.getServerJarFileName());

        if(!serverJarFile.exists())
            throw new IllegalStateException(String.format("Server %s does not have jar file(expected with name %s)",
                    serverName,config.getServerJarFileName()));

        File eulaFile=new File(serverDir, EULA_FILE_NAME);
        if(!eulaFile.exists())
            throw new IllegalStateException(String.format("Server %s does not have eula.txt", serverName));

        Map<String,String> eulaConfig= ConfigurationUtils.readConfiguration(eulaFile);
        String eula=eulaConfig.get("eula");
        if(eula==null || !eula.equals("true"))
            throw new IllegalArgumentException("Eula is false, please read license and accept and start server again");

        File propertiesFile=new File(serverDir,SERVER_PROPERTIES_NAME);
        if(!propertiesFile.exists())
            throw new IllegalStateException(String.format("Server %s does not have server.properties", serverName));

        if(!new File(config.getJavaPath()).exists())
            throw new IllegalStateException(String.format("Server %s 's java does not exist(expected in %s)",
                    serverName,config.getJavaPath()));
    }

    public void applyConfigForServer(MinecraftServerConfig config) throws Exception{
        String serverName=config.getServerName();

        File serverDir=new File(serverName+"/");

        //Prepare jar file
        //Priority:
        //1.If server jar file exists in the server dir, skip this step
        //2.Find the server jar file with the same name that config contains
        //3.If there is not such a file, use file name defined in env
        File serverJarFile=new File(serverDir,config.getServerJarFileName());
        if(!serverJarFile.exists()){
            File defaultJarFile=new File(config.getServerJarFileName());
            if(!defaultJarFile.exists())
                defaultJarFile=new File(defaultEnvConfig.getServerJarFileName());

            if(!defaultJarFile.exists()){
                throw new MinecraftServerConfigException(String.format("No jar file with name %s was found, can not apply config for server %s",
                        defaultJarFile.getName(),serverName));
            }

            Files.copy(defaultJarFile.toPath(),serverJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        //Prepare eula
        File eulaFile=new File(serverDir,EULA_FILE_NAME);
        File defaultEula=new File(EULA_FILE_NAME);
        Files.copy(defaultEula.toPath(),eulaFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

        //Prepare server.properties
        File propertiesFile=new File(serverDir,SERVER_PROPERTIES_NAME);
        File defaultPropertiesFile=new File(SERVER_PROPERTIES_NAME);
        Files.copy(defaultPropertiesFile.toPath(),propertiesFile.toPath(),StandardCopyOption.REPLACE_EXISTING);

        Map<String,String> properties= ConfigurationUtils.readConfiguration(propertiesFile);
        Map<String,String> customizedProperties=config.getCustomizedServerProperties();
        properties.putAll(customizedProperties);
        ConfigurationUtils.writeConfiguration(propertiesFile,properties);

        //Copy other necessary files
        String needCopyFileNamesTotal=config.getAutoConfigCopy();
        if(needCopyFileNamesTotal!=null && !needCopyFileNamesTotal.equals("")){
            String[] needCopyFileNames=needCopyFileNamesTotal.split(",");
            for(String fileName:needCopyFileNames){
                File origin=new File(fileName);
                File dst=new File(serverDir,fileName);

                //Check if the origin exists
                if(!origin.exists()){
                    throw new MinecraftServerConfigException(String.format("Necessary copy file %s does not exist", fileName));
                }

                //Copy file
                if(!origin.isDirectory()){
                    Files.copy(origin.toPath(),dst.toPath(),StandardCopyOption.REPLACE_EXISTING);
                }else{
                    FileUtils.copyDir(origin,dst);
                }
            }
        }

        //Copy other optional files
        needCopyFileNamesTotal=config.getOptionalAutoConfigCopy();
        if(needCopyFileNamesTotal!=null && !needCopyFileNamesTotal.equals("")){
            String[] needCopyFileNames=needCopyFileNamesTotal.split(",");
            for(String fileName:needCopyFileNames){
                File origin=new File(fileName);
                File dst=new File(serverDir,fileName);

                //Copy file
                if(!origin.isDirectory()){
                    Files.copy(origin.toPath(),dst.toPath(),StandardCopyOption.REPLACE_EXISTING);
                }else{
                    FileUtils.copyDir(origin,dst);
                }
            }
        }
    }
}
