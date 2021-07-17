package com.qzero.server.config.manager;

import com.qzero.server.config.IConfigurationManager;

public interface IServerManagerConfigurationManager extends IConfigurationManager {
    ServerManagerConfiguration getManagerConfiguration();
}
