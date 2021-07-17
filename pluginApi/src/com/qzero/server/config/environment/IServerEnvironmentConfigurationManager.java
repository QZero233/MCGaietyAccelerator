package com.qzero.server.config.environment;

import com.qzero.server.config.IConfigurationManager;

public interface IServerEnvironmentConfigurationManager extends IConfigurationManager {
    ServerEnvironment getServerEnvironment();
}
