package com.qzero.server.config.authorize;

import com.qzero.server.config.IConfigurationManager;

import java.io.File;

public class AuthorizeConfigurationManager implements IConfigurationManager {

    public static final String AUTHORIZE_CONFIG_FILE_DIR="authorize/";

    static {
        new File(AUTHORIZE_CONFIG_FILE_DIR).mkdirs();
    }

    @Override
    public void loadConfig() throws Exception {
        //TODO Load authorize config
        File dir=new File(AUTHORIZE_CONFIG_FILE_DIR);
    }
}
