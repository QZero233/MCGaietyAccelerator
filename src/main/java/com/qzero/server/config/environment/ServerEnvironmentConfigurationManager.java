package com.qzero.server.config.environment;

import com.qzero.server.config.IConfigurationManager;
import com.qzero.server.utils.ConfigurationUtils;

import java.io.File;
import java.util.Map;

public class ServerEnvironmentConfigurationManager implements IConfigurationManager {

    public static final String ENV_CONFIG_FILE_NAME="env.config";

    private ServerEnvironment environment;

    @Override
    public void loadConfig() throws Exception {
        //Load server environment
        File envFile=new File(ENV_CONFIG_FILE_NAME);
        Map<String,String> envConfig= ConfigurationUtils.readConfiguration(envFile);

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

    public ServerEnvironment getServerEnvironment() {
        return environment;
    }

}
