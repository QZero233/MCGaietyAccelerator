package com.qzero.server.config.mcga;

import com.qzero.server.config.IConfigurationManager;
import com.qzero.server.utils.ConfigurationUtils;

import java.io.File;
import java.util.Map;

public class MCGAConfigurationManager implements IConfigurationManager {

    private MCGAConfiguration mcgaConfiguration;

    public static final String MCGA_CONFIG_FILE_NAME ="mcga.config";

    @Override
    public void loadConfig() throws Exception {
        File file=new File(MCGA_CONFIG_FILE_NAME);
        if(!file.exists())
            throw new IllegalStateException("MCGA config file does not exist");

        Map<String,String> config= ConfigurationUtils.readConfiguration(file);
        if(config==null)
            throw new IllegalStateException("Manager config file can not be empty");

        mcgaConfiguration=new MCGAConfiguration(config.get("containerName"),config);
    }

    public MCGAConfiguration getMcgaConfiguration() {
        return mcgaConfiguration;
    }

}
