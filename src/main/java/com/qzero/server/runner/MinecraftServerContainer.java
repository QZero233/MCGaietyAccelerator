package com.qzero.server.runner;

public interface MinecraftServerContainer {

    MinecraftServerOperator getServerOperator(String serverName);

}
