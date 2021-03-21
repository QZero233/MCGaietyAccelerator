package com.qzero.server.console.log;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Plugin(name = "GameLogAppender", category = "Core", elementType = "appender", printObject = true)
public class GameLogOutputAppender extends AbstractAppender {

    private static Map<String, GameLogListener> gameLogListenerMap=new HashMap<>();


    private GameLogOutputAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    @PluginFactory
    public static GameLogOutputAppender createAppender(@PluginAttribute("name") String name,
                                                       @PluginElement("Layout") Layout layout,
                                        @PluginElement("Filters") Filter filter){
        return new GameLogOutputAppender(name,filter,layout);
    }

    @Override
    public void append(LogEvent logEvent) {
        String message=logEvent.getMessage().getFormattedMessage();
        Set<String> keySet=gameLogListenerMap.keySet();
        for(String key:keySet){
            GameLogListener logListener=gameLogListenerMap.get(key);
            logListener.log(message);
        }
    }

    public static void registerLogListener(GameLogListener listener){
        gameLogListenerMap.put(listener.getListenerId(),listener);
    }

    public static void unregisterLogListener(String listenerId){
        gameLogListenerMap.remove(listenerId);
    }
}
