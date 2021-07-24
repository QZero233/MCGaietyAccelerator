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

    enum PlayerEvent{
        JOIN,
        LEAVE
    }

    String getListenerId();

    default void receivedOutputLine(String serverName,String outputLine,OutputType outputType){

    }

    default void receivedServerEvent(String serverName,ServerEvent event){

    }

    default void receivedPlayerEvent(String serverName,String playerName,PlayerEvent event){

    }

    default boolean isSingleTimeEventListener(){
        return false;
    }

}
