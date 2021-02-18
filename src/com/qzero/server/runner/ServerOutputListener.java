package com.qzero.server.runner;

public interface ServerOutputListener {

    enum  OutputType{
        TYPE_NORMAL,
        TYPE_ERROR
    }

    String getListenerId();

    void receivedOutputLine(String serverName,String outputLine,OutputType outputType);

}
