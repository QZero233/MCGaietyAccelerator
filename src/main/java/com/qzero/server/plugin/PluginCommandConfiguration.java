package com.qzero.server.plugin;

public class PluginCommandConfiguration {

    enum ConfigType{
        TYPE_CLASS,
        TYPE_METHOD
    }

    private ConfigType configType;
    private Object configObj;

    public PluginCommandConfiguration(ConfigType configType, Object configObj) {
        this.configType = configType;
        this.configObj = configObj;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public Object getConfigObj() {
        return configObj;
    }

    public void setConfigObj(Object configObj) {
        this.configObj = configObj;
    }

    @Override
    public String toString() {
        return "PluginCommandConfiguration{" +
                "configType=" + configType +
                ", configObj=" + configObj +
                '}';
    }
}
