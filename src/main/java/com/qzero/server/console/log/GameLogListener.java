package com.qzero.server.console.log;

public interface GameLogListener {
    String getListenerId();
    void log(String log);

}
