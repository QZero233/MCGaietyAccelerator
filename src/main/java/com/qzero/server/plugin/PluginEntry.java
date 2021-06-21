package com.qzero.server.plugin;

import java.util.List;

public interface PluginEntry {

    void loadOperateHelper(PluginServerOperateHelper helper);

    List<PluginCommandConfiguration> getCommandConfigurations();

}
