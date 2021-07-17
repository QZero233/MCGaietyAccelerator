package com.qzero.server.config.authorize;

import com.qzero.server.config.IConfigurationManager;
import com.qzero.server.utils.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuthorizeConfigurationManager implements IConfigurationManager {

    private Logger log= LoggerFactory.getLogger(getClass());

    public static final String AUTHORIZE_CONFIG_FILE_DIR="authorize/";

    static {
        new File(AUTHORIZE_CONFIG_FILE_DIR).mkdirs();
    }

    private Map<String,AdminConfig> adminConfigMap=new HashMap<>();

    @Override
    public void loadConfig() throws Exception {
        File dir=new File(AUTHORIZE_CONFIG_FILE_DIR);

        File[] files=dir.listFiles();
        for(File file:files){
            if(!file.getName().endsWith(".config"))
                continue;

            String adminName=file.getName().replace(".config","");
            try {
                Map<String,String> config= ConfigurationUtils.readConfiguration(file);
                if(config==null)
                    continue;

                AdminConfig adminConfig=ConfigurationUtils.configToJavaBeanWithOnlyStringFields(config,AdminConfig.class);
                adminConfig.setAdminLevelInInt(Integer.parseInt(adminConfig.getAdminLevel()));
                adminConfigMap.put(adminName,adminConfig);
                log.debug("Loaded config for admin "+adminName);
            }catch (Exception e){
                log.error("Failed to load admin config for operator "+adminName,e);
                continue;
            }
        }

        AdminConfig localConsole=new AdminConfig();
        localConsole.setAdminLevelInInt(3);
        adminConfigMap.put("#localConsole",localConsole);
    }


    public boolean checkAdminInfo(String adminName, String passwordHash){
        AdminConfig adminConfig=adminConfigMap.get(adminName);
        if(adminConfig==null)
            return false;

        if(!adminConfig.getPasswordHash().equals(passwordHash))
            return false;

        return true;
    }


    public AdminConfig getAdminConfig(String adminName){
        return adminConfigMap.get(adminName);
    }


    public Set<String> getAdminNameList(){
        return adminConfigMap.keySet();
    }


    public void removeAdmin(String adminName){
        adminConfigMap.remove(adminName);
        File file=new File(AUTHORIZE_CONFIG_FILE_DIR+adminName+".config");
        if(file.exists())
            file.delete();
    }


    public void addAdmin(String adminName, AdminConfig config) throws IOException {
        File file=new File(AUTHORIZE_CONFIG_FILE_DIR+adminName+".config");

        Map<String,String> configMap=new HashMap<>();
        configMap.put("passwordHash",config.getPasswordHash());
        configMap.put("adminLevel",config.getAdminLevel());
        ConfigurationUtils.writeConfiguration(file,configMap);

        adminConfigMap.put(adminName,config);
    }

}
