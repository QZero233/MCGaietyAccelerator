package com.qzero.server.runner;

public interface ServerOutputListener {

    enum ServerEvent{
        SERVER_STARTING,
        SERVER_STARTED,
        SERVER_STOPPED,
    };

    enum  OutputType{
        TYPE_NORMAL,
        TYPE_ERROR
    }

    String getListenerId();

    void receivedOutputLine(String serverName,String outputLine,OutputType outputType);

    void receivedServerEvent(String serverName,ServerEvent event);

}
