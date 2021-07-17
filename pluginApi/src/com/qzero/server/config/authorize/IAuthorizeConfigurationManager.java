package com.qzero.server.config.authorize;

import com.qzero.server.config.IConfigurationManager;

import java.io.IOException;
import java.util.Set;

public interface IAuthorizeConfigurationManager extends IConfigurationManager {
    boolean checkAdminInfo(String adminName, String passwordHash);

    AdminConfig getAdminConfig(String adminName);

    Set<String> getAdminNameList();

    void removeAdmin(String adminName);

    void addAdmin(String adminName, AdminConfig config) throws IOException;
}
